package com.training.dunningcuring.billing.service;

import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.invoice.entity.Invoice;
import com.training.dunningcuring.invoice.entity.InvoiceLineItem;
import com.training.dunningcuring.invoice.entity.InvoiceStatus;
import com.training.dunningcuring.invoice.repository.InvoiceRepository;
import com.training.dunningcuring.plan.entity.Subscription;
import com.training.dunningcuring.plan.entity.SubscriptionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BillingService {

    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;

    public BillingService(CustomerRepository customerRepository,
                          InvoiceRepository invoiceRepository) {
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * This method NO LONGER creates invoices.
     * It ONLY updates customer overdue summaries based on existing invoices.
     */
    @Transactional
    public void runBillingCycleUpdate() {
        logger.info("--- Starting Billing Cycle (Updating Statuses) ---");

        // Job: Update all customer overdue summaries
        updateCustomerOverdueStatus();

        logger.info("--- Billing Cycle (Updating Statuses) Finished ---");
    }

    /**
     * This is the dedicated method for the Admin button.
     * It FORCES invoice creation, bypassing any date checks.
     */
    @Transactional
    public void createMonthlyInvoices() {
        logger.info("--- Admin forcing creation of monthly invoices ---");

        List<Customer> activePostpaidCustomers = customerRepository.findAll().stream()
                .filter(c -> c.getSegment() == CustomerSegment.POSTPAID &&
                        (c.getStatus() == ServiceStatus.ACTIVE ||
                                c.getStatus() == ServiceStatus.THROTTLED))
                .toList();

        for (Customer customer : activePostpaidCustomers) {
            List<Subscription> activeSubs = customer.getSubscriptions().stream()
                    .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                    .toList();

            if (activeSubs.isEmpty()) {
                continue; // No active plans, no invoice
            }

            // Create new Invoice
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(10);
            Invoice invoice = new Invoice(customer, issueDate, dueDate);

            // Add line items for each subscription
            for (Subscription sub : activeSubs) {
                invoice.addLineItem(new InvoiceLineItem(
                        sub.getPlan().getPlanName(),
                        sub.getPlan().getPrice()
                ));
            }

            invoiceRepository.save(invoice);
            logger.info("Created Invoice {} for Customer {} with amount ${}",
                    invoice.getId(), customer.getId(), invoice.getTotalAmount());
        }
    }


    /**
     * This is the internal logic for updating customer summaries.
     */
    private void updateCustomerOverdueStatus() {
        List<Customer> allCustomers = customerRepository.findAll();

        for (Customer customer : allCustomers) {
            List<Invoice> unpaidInvoices = invoiceRepository.findByCustomerAndStatusIn(
                    customer, List.of(InvoiceStatus.ISSUED, InvoiceStatus.OVERDUE)
            );

            if (unpaidInvoices.isEmpty()) {
                // No unpaid invoices, clear their debt
                if(customer.getAmountOverdue().compareTo(BigDecimal.ZERO) != 0) {
                    customer.setAmountOverdue(BigDecimal.ZERO);
                    customer.setOverdueDays(0);
                    customer.setDueDate(null);
                    customerRepository.save(customer);
                }
            } else {
                // They have unpaid invoices, let's calculate
                BigDecimal totalDue = unpaidInvoices.stream()
                        .map(Invoice::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                LocalDate oldestDueDate = unpaidInvoices.stream()
                        .map(Invoice::getDueDate)
                        .min(LocalDate::compareTo)
                        .orElse(LocalDate.now());

                int overdueDays = 0;
                if (LocalDate.now().isAfter(oldestDueDate)) {
                    overdueDays = (int) ChronoUnit.DAYS.between(oldestDueDate, LocalDate.now());
                }

                customer.setAmountOverdue(totalDue);
                customer.setOverdueDays(overdueDays);
                customer.setDueDate(oldestDueDate);
                customerRepository.save(customer);

                // Also, mark any ISSUED invoices as OVERDUE if they are past due
                for (Invoice inv : unpaidInvoices) {
                    if (inv.getStatus() == InvoiceStatus.ISSUED && LocalDate.now().isAfter(inv.getDueDate())) {
                        inv.setStatus(InvoiceStatus.OVERDUE);
                        invoiceRepository.save(inv);
                        logger.info("Invoice {} is now OVERDUE", inv.getId());
                    }
                }
            }
        }
    }
}