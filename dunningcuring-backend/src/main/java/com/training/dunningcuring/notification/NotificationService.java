package com.training.dunningcuring.notification;

import com.training.dunningcuring.customer.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * This is the method your DunningEngine is looking for.
     */
    public void sendDunningNotification(Customer customer, String message) {
        // Mock sending an SMS/Email by logging it to the console
        logger.info(">>> NOTIFICATION to {}: {}", customer.getEmail(), message);
    }

    public void sendCuringNotification(Customer customer, String message) {
        // Mock sending an SMS/Email
        logger.info(">>> NOTIFICATION to {}: {}", customer.getEmail(), message);
    }
}