package com.training.dunningcuring.dunning.service;

import com.training.dunningcuring.audit.service.AuditLogService;
import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.dunning.entity.DunningAction;
import com.training.dunningcuring.dunning.entity.DunningRule;
import com.training.dunningcuring.dunning.repository.DunningRuleRepository;
import com.training.dunningcuring.notification.NotificationService; // <-- IMPORT
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DunningEngineService {

    private static final Logger logger = LoggerFactory.getLogger(DunningEngineService.class);

    private final CustomerRepository customerRepository;
    private final DunningRuleRepository ruleRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService; // <-- ADD THIS

    // --- UPDATE CONSTRUCTOR ---
    public DunningEngineService(CustomerRepository customerRepository,
                                DunningRuleRepository ruleRepository,
                                AuditLogService auditLogService,
                                NotificationService notificationService) { // <-- ADD THIS
        this.customerRepository = customerRepository;
        this.ruleRepository = ruleRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService; // <-- ADD THIS
    }

    //    @Scheduled(fixedRate = 120000, initialDelay = 60000) // Stays commented out
    @Transactional
    public void runDunningCycle() {
        logger.info("--- Starting Dunning Cycle ---");

        List<DunningRule> activeRules = ruleRepository.findAll().stream()
                .filter(DunningRule::isActive)
                .toList();

        if (activeRules.isEmpty()) {
            logger.error("No active dunning rules found. Skipping cycle.");
            return;
        }

        List<Customer> overdueCustomers = customerRepository.findAll().stream()
                .filter(c -> c.getOverdueDays() > 0 &&
                        c.getStatus() != ServiceStatus.BLOCKED)
                .toList();

        logger.info("Found {} active rules and {} overdue customers to process.",
                activeRules.size(), overdueCustomers.size());

        for (Customer customer : overdueCustomers) {
            processCustomer(customer, activeRules);
        }

        logger.info("--- Dunning Cycle Finished ---");
    }

    private void processCustomer(Customer customer, List<DunningRule> rules) {
        for (DunningRule rule : rules) {
            if (ruleMatchesCustomer(customer, rule)) {
                applyDunningAction(customer, rule);
                break;
            }
        }
    }

    /**
     * UPDATED to include maxOverdueDays
     */
    private boolean ruleMatchesCustomer(Customer customer, DunningRule rule) {
        boolean segmentMatch = rule.getTargetSegment() == CustomerSegment.ALL ||
                rule.getTargetSegment() == customer.getSegment();

        boolean daysMatch = customer.getOverdueDays() >= rule.getMinOverdueDays() &&
                customer.getOverdueDays() <= rule.getMaxOverdueDays();

        // boolean riskMatch = customer.getPaymentRiskScore() >= rule.getMinRiskScore(); // <-- DELETE THIS LINE

        return segmentMatch && daysMatch; // <-- SIMPLIFY THIS LINE
    }

    /**
     * REPLACED to handle new actions and notifications
     */
    private void applyDunningAction(Customer customer, DunningRule rule) {
        DunningAction action = rule.getActionToTake();
        ServiceStatus currentStatus = customer.getStatus();
        ServiceStatus newStatus = currentStatus;
        String logMessage;
        String notificationMessage = null;

        switch (action) {
            case NOTIFY_THROTTLE:
                logMessage = "Action: Sent throttle warning.";
                notificationMessage = "Your account is overdue. Please pay to avoid service throttling.";
                break;

            case THROTTLE_DATA:
                newStatus = ServiceStatus.THROTTLED;
                logMessage = "Action: Service THROTTLED.";
                notificationMessage = "Your service has been throttled due to non-payment.";
                break;

            case BLOCK_VOICE:
            case BLOCK_ALL_SERVICES:
                newStatus = ServiceStatus.BLOCKED;
                logMessage = "Action: Service BLOCKED.";
                notificationMessage = "Your service has been blocked due to non-payment.";
                break;

            case SEND_SMS:
            case SEND_EMAIL:
            default:
                logMessage = "Action: Sent general notification.";
                notificationMessage = "This is a friendly reminder about your overdue bill.";
                break;
        }

        if (currentStatus != newStatus) {
            customer.setStatus(newStatus);
            customerRepository.save(customer);

            logger.info("Applied Action: {} to Customer ID: {}. New Status: {}",
                    rule.getActionToTake(), customer.getId(), newStatus);

            auditLogService.logEvent(
                    customer, newStatus.name(), "DUNNING_ENGINE",
                    "Triggered by Rule ID: " + rule.getId() + " (" + rule.getRuleName() + ")"
            );

            if (notificationMessage != null) {

                notificationService.sendDunningNotification(customer, notificationMessage);
            }

        } else if (notificationMessage != null) {
            logger.info("Sent Notification: {} to Customer ID: {}",
                    rule.getActionToTake(), customer.getId());

            auditLogService.logEvent(
                    customer, "NOTIFICATION_SENT", "DUNNING_ENGINE",
                    "Triggered by Rule ID: " + rule.getId() + " (" + rule.getRuleName() + ")"
            );
            // This is the line that was causing the error
            notificationService.sendDunningNotification(customer, notificationMessage);
        }
    }
}
