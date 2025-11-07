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
import com.training.dunningcuring.plan.dto.PlanDTO; // <-- NEW IMPORT
import com.training.dunningcuring.plan.service.PlanService;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DunningTools {

    
    private final CustomerService customerService;
    private final PlanService planService;
    
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            log.warn("AI Tool: Could not retrieve authenticated user.");
            throw new IllegalStateException("User not authenticated");
        }
        // The "name" of the principal is the username (email)
        return authentication.getName();
    }
    
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

    @Tool("Fetches all available plans that the customer can subscribe to.")
    public String getAvailableSubscriptionPlans() {
        log.info("AI Tool: Executing getAvailableSubscriptionPlans");
        try {
            // 1. Get the logged-in user's name
            String username = getAuthenticatedUsername();

            // 2. Pass the username to the service method
            List<PlanDTO> plans = planService.getAvailablePlans(username);

            if (plans == null || plans.isEmpty()) {
                return "There are currently no plans available for subscription.";
            }

            // 3. Format the list using the correct DTO field names
            String planList = plans.stream()
                    .map(plan -> String.format(
                            "Plan Name: %s, Price: $%.2f/month, Service: %s, Data: %.0fMB",
                            plan.getPlanName(),
                            plan.getPrice(),
                            plan.getType(), // Use 'type' from your DTO
                            plan.getDataLimitMb() // Use 'dataLimitMb' from your DTO
                    ))
                    .collect(Collectors.joining("; ")); // Separate with semicolons

            return "Here are the available plans: " + planList;

        } catch (IllegalStateException e) {
            log.warn("AI Tool: User not authenticated.");
            return "Error: User is not authenticated. Please log in.";
        } catch (Exception e) {
            log.error("AI Tool: Error fetching available plans", e);
            return "Error: Could not retrieve available plans at this time.";
        }
    }
}
