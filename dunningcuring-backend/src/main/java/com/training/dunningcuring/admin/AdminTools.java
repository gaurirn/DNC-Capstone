package com.training.dunningcuring.ai;

import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.payment.entity.PaymentType;
import com.training.dunningcuring.payment.repository.PaymentRepository;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminTools {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    @Tool("Finds a specific customer by their email address and returns their full status.")
    public String findCustomerByEmail(String email) {
        log.info("AI Tool: Executing findCustomerByEmail for {}", email);
        try {
            Customer customer = customerRepository.findByEmail(email)
                    .orElse(null);

            if (customer == null) {
                return String.format("No customer found with email: %s", email);
            }

            // Format a summary for the admin
            return String.format(
                    "Customer Found: %s %s (ID: %d). Status: %s. Segment: %s. Overdue Amount: $%.2f. Overdue Days: %d. Wallet Balance: $%.2f.",
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getId(),
                    customer.getStatus(),
                    customer.getSegment(),
                    customer.getAmountOverdue(),
                    customer.getOverdueDays(),
                    customer.getBalance()
            );
        } catch (Exception e) {
            log.error("AI Tool: Error finding customer", e);
            return "Error: Could not retrieve customer data.";
        }
    }

    @Tool("Gets high-level statistics about the entire system, including total customers, revenue, and at-risk accounts.")
    public String getSystemStatistics() {
        log.info("AI Tool: Executing getSystemStatistics");
        try {
            long totalCustomers = customerRepository.count();
            long throttled = customerRepository.countByStatus(ServiceStatus.THROTTLED);
            long blocked = customerRepository.countByStatus(ServiceStatus.BLOCKED);
            BigDecimal revenue = paymentRepository.findByTypeWithCustomerOrderByPaymentDateDesc(PaymentType.INVOICE_PAYMENT)
                    .stream()
                    .map(p -> p.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return String.format(
                    "System Stats: Total Customers: %d. Total Revenue Recovered: $%.2f. Accounts Throttled: %d. Accounts Blocked: %d.",
                    totalCustomers,
                    revenue,
                    throttled,
                    blocked
            );
        } catch (Exception e) {
            log.error("AI Tool: Error getting system stats", e);
            return "Error: Could not retrieve system statistics.";
        }
    }
}