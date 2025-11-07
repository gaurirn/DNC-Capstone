package com.training.dunningcuring.billing.service;

import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.plan.entity.Subscription;
import com.training.dunningcuring.plan.entity.SubscriptionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsageService {

    private static final Logger logger = LoggerFactory.getLogger(UsageService.class);
    private final CustomerRepository customerRepository;

    public UsageService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Simulates real-time data usage for PREPAID customers.
     * Runs every 10 seconds.
     */
//    @Scheduled(fixedRate = 10000) // 10,000 ms = 10 seconds
    @Transactional
    public void simulateDataUsage() {
        // Use INFO so we can see it in the console
        logger.info("--- Simulating Data Usage ---");

        List<Customer> prepaidCustomers = customerRepository.findAll().stream()
                .filter(c -> c.getSegment() == CustomerSegment.PREPAID && c.getStatus() == ServiceStatus.ACTIVE)
                .toList();

        for (Customer customer : prepaidCustomers) {
            // Find their total data limit from all plans
            double totalLimitMb = customer.getSubscriptions().stream()
                    .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                    .mapToDouble(s -> s.getPlan().getDataLimitMb())
                    .sum();

            if (totalLimitMb == 0) continue; // No data limit, skip

            // Add 100MB of simulated usage
            customer.setDataUsageMb(customer.getDataUsageMb() + 100);
            logger.info("Usage for {}: {}/{} MB", customer.getEmail(), customer.getDataUsageMb(), totalLimitMb);

            // Check if they've gone over the limit
            if (customer.getDataUsageMb() > totalLimitMb) {
                customer.setStatus(ServiceStatus.THROTTLED); // Immediately throttle!
                logger.info(">>> PREPAID DUNNING: Customer ID {} hit data limit. Status set to THROTTLED.", customer.getId());
            }

            // --- THIS IS THE FIX ---
            // Save the customer regardless of status
            // This will save the new dataUsageMb every 10 seconds.
            customerRepository.save(customer);
        }
    }
}