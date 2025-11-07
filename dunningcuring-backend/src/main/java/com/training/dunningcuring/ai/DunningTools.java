package com.training.dunningcuring.ai;

import com.training.dunningcuring.customer.dto.CustomerStatusDTO;
import com.training.dunningcuring.customer.service.CustomerService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DunningTools {

    // This is our existing CustomerService
    private final CustomerService customerService;

    /**
     * Helper to get the username of the logged-in user.
     * Our CustomerService.getCustomerStatus() method takes a username string.
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            log.warn("AI Tool: Could not retrieve authenticated user.");
            throw new IllegalStateException("User not authenticated");
        }
        // The "name" of the principal is the username (email)
        return authentication.getName();
    }

    /**
     * This is the one and only tool the AI needs.
     * It calls our getCustomerStatus() method and formats the result into a simple string.
     */
    @Tool("Fetches the current customer's complete account status, including profile (status, balance, overdue amount), active subscriptions, and unpaid invoices.")
    public String getCurrentAccountStatus() {
        log.info("AI Tool: Executing getCurrentAccountStatus");
        try {
            // 1. Get the username
            String username = getAuthenticatedUsername();

            // 2. Call our existing service method
            CustomerStatusDTO status = customerService.getCustomerStatus(username);

            // 3. Format the DTO into a simple string for the AI
            StringBuilder summary = new StringBuilder();
            summary.append(String.format("Customer Name: %s %s. ", status.getProfile().getFirstName(), status.getProfile().getLastName()));
            summary.append(String.format("Account Status: %s. ", status.getProfile().getStatus()));
            summary.append(String.format("Current Wallet Balance: $%.2f. ", status.getProfile().getBalance()));
            summary.append(String.format("Total Amount Overdue: $%.2f. ", status.getProfile().getAmountOverdue()));

            if (status.getProfile().getDueDate() != null) {
                summary.append(String.format("Next Due Date: %s. ", status.getProfile().getDueDate()));
            }

            if (status.getActiveSubscriptions().isEmpty()) {
                summary.append("Active Subscriptions: None. ");
            } else {
                summary.append("Active Subscriptions: ");
                // Get plan name and price from the DTO
                String plans = status.getActiveSubscriptions().stream()
                        .map(sub -> String.format("%s at $%.2f/mo", sub.getPlan().getPlanName(), sub.getPlan().getPrice()))
                        .collect(Collectors.joining(", "));
                summary.append(plans).append(". ");
            }

            if (status.getUnpaidInvoices().isEmpty()) {
                summary.append("Unpaid Invoices: None.");
            } else {
                summary.append(String.format("Unpaid Invoices: %d.", status.getUnpaidInvoices().size()));
            }

            return summary.toString();

        } catch (IllegalStateException e) {
            log.warn("AI Tool: User not authenticated.");
            return "Error: User is not authenticated. Please log in.";
        } catch (Exception e) {
            log.error("AI Tool: Error fetching customer status", e);
            return "Error: Could not retrieve account information at this time.";
        }
    }
}