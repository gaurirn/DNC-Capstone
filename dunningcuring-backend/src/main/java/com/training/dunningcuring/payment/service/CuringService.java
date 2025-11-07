package com.training.dunningcuring.payment.service;

import com.training.dunningcuring.audit.service.AuditLogService;
import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.exception.InsufficientBalanceException;
import com.training.dunningcuring.invoice.entity.Invoice;
import com.training.dunningcuring.invoice.entity.InvoiceStatus;
import com.training.dunningcuring.invoice.repository.InvoiceRepository;
import com.training.dunningcuring.notification.NotificationService;
import com.training.dunningcuring.payment.entity.Payment;
import com.training.dunningcuring.payment.entity.PaymentType;
import com.training.dunningcuring.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuringService {

    private static final Logger logger = LoggerFactory.getLogger(CuringService.class);
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final InvoiceRepository invoiceRepository;

    public CuringService(CustomerRepository customerRepository,
                         PaymentRepository paymentRepository,
                         AuditLogService auditLogService,
                         NotificationService notificationService,
                         InvoiceRepository invoiceRepository) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public void processPaymentAndCure(Customer customer, String source) {
        BigDecimal amountToPay = customer.getAmountOverdue();

        // 1. Check if they have enough balance
        if (customer.getBalance().compareTo(amountToPay) < 0) {
            logger.error("Curing failed for Customer ID: {}. Insufficient balance. Has: {}, Needs: {}",
                    customer.getId(), customer.getBalance(), amountToPay);
            throw new InsufficientBalanceException("Insufficient balance. Please add funds to your wallet first.");
        }

        logger.info("Processing payment for Customer ID: {} from {}", customer.getId(), source);

        // 2. Deduct from balance
        customer.setBalance(customer.getBalance().subtract(amountToPay));

        // 3. Create a Payment record for this *internal* transfer
        Payment payment = new Payment(customer, amountToPay, source, PaymentType.INVOICE_PAYMENT);
        paymentRepository.save(payment);

        // 4. Mark all open invoices as PAID
        List<Invoice> unpaidInvoices = invoiceRepository.findByCustomerAndStatusIn(
                customer, List.of(InvoiceStatus.ISSUED, InvoiceStatus.OVERDUE)
        );
        for (Invoice invoice : unpaidInvoices) {
            invoice.setStatus(InvoiceStatus.PAID);
        }
        invoiceRepository.saveAll(unpaidInvoices);
        logger.info("Marked {} invoices as PAID for Customer ID: {}", unpaidInvoices.size(), customer.getId());

        // 5. Reset the customer's debt summary
        customer.setAmountOverdue(BigDecimal.ZERO);
        customer.setOverdueDays(0);
        customer.setDueDate(null);

        // 6. Check if their status needs curing
        if (customer.getStatus() != ServiceStatus.ACTIVE) {
            customer.setStatus(ServiceStatus.ACTIVE);
            logger.info("Curing Customer ID: {}. Status set to ACTIVE.", customer.getId());

            auditLogService.logEvent(
                    customer, "CURED", source,
                    "Service restored to ACTIVE."
            );

            notificationService.sendCuringNotification(customer, "Your service has been restored. Thank you for your payment.");
        }

        // 7. Save all changes
        customerRepository.save(customer);

        // 8. Log the internal transfer
        auditLogService.logEvent(
                customer, "BILL_PAID", source,
                "Used balance of $" + amountToPay + " to pay all open invoices."
        );
    }

    /**
     * This is the "add money to wallet" method.
     */
    @Transactional
    public Customer addBalance(Customer customer, BigDecimal amount, String source) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        // 1. Add to balance
        customer.setBalance(customer.getBalance().add(amount));

        // 2. Create a Payment record as proof
        // --- THIS IS THE FIX ---
        Payment payment = new Payment(customer, amount, source, PaymentType.TOP_UP);
        // --- END OF FIX ---
        paymentRepository.save(payment);

        // 3. DO NOT change status. Subscribing changes status.
        Customer savedCustomer = customerRepository.save(customer);

        auditLogService.logEvent(
                customer,
                "BALANCE_ADDED",
                source,
                "Added $" + amount + ". New balance: $" + savedCustomer.getBalance()
        );

        return savedCustomer;
    }

    @Transactional
    public void processDataTopUp(Customer customer, String source) {
        // This logic remains the same
        if (customer.getStatus() == ServiceStatus.THROTTLED && customer.getSegment() == CustomerSegment.PREPAID) {
            customer.setStatus(ServiceStatus.ACTIVE);
            customer.setDataUsageMb(0);
            customerRepository.save(customer);

            logger.info("Curing PREPAID Customer ID: {}. Data usage reset. Status set to ACTIVE.", customer.getId());
            auditLogService.logEvent(
                    customer,
                    "DATA_CURED",
                    source,
                    "Data top-up applied."
            );

            notificationService.sendCuringNotification(customer, "Your data top-up is complete. Your service speed is restored.");
        } else {
            logger.info("Top-up applied for already ACTIVE PREPAID Customer ID: {}.", customer.getId());
        }
    }
}
