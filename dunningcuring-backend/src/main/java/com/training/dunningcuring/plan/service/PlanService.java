package com.training.dunningcuring.plan.service;

import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.exception.ResourceNotFoundException;
import com.training.dunningcuring.payment.entity.Payment;
import com.training.dunningcuring.payment.entity.PaymentType; // <-- Make sure this is imported
import com.training.dunningcuring.plan.dto.PlanDTO;
import com.training.dunningcuring.plan.entity.Plan;
import com.training.dunningcuring.plan.entity.Subscription;
import com.training.dunningcuring.plan.entity.SubscriptionStatus;
import com.training.dunningcuring.plan.mapper.PlanMapper;
import com.training.dunningcuring.plan.repository.PlanRepository;
import com.training.dunningcuring.plan.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; // <-- Import this

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;
    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PlanService(PlanRepository planRepository,
                       PlanMapper planMapper,
                       CustomerRepository customerRepository,
                       SubscriptionRepository subscriptionRepository) {
        this.planRepository = planRepository;
        this.planMapper = planMapper;
        this.customerRepository = customerRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    // This is for your CustomerPortalController's /api/me/plans
    @Transactional(readOnly = true)
    public List<PlanDTO> getAvailablePlans(String username) {
        Customer customer = getCustomerFromUsername(username);
        // Find plans that are active AND match the customer's segment
        List<Plan> plans = planRepository.findByIsActiveTrueAndSegment(customer.getSegment());
        return planMapper.toDtoList(plans);
    }

    // This is for your CustomerPortalController's /api/me/subscriptions
    @Transactional(readOnly = true)
    public List<Subscription> getMySubscriptions(String username) {
        Customer customer = getCustomerFromUsername(username);
        return customer.getSubscriptions();
    }

    @Transactional
    public Subscription subscribeToPlan(String username, Long planId) {
        Customer customer = getCustomerFromUsername(username);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));

        if (customer.getSegment() != plan.getSegment()) {
            throw new RuntimeException("This plan is not available for your account type.");
        }
        if (customer.getSegment() == CustomerSegment.PREPAID) {
            if (customer.getBalance().compareTo(plan.getPrice()) < 0) {
                throw new RuntimeException("Insufficient balance to subscribe. Please add funds.");
            }
            customer.setBalance(customer.getBalance().subtract(plan.getPrice()));

            // --- THIS IS THE FIX ---
            // A plan purchase for a PREPAID user is a TOP_UP
            Payment payment = new Payment(customer, plan.getPrice(), PaymentType.TOP_UP);
            // --- END OF FIX ---

            customer.addPayment(payment);
        }
        Subscription newSubscription = new Subscription(customer, plan);
        customer.addSubscription(newSubscription);
        customer.setStatus(ServiceStatus.ACTIVE);

        customerRepository.save(customer); // Save the customer
        return newSubscription;
    }

    @Transactional
    public void cancelSubscription(String username, Long subscriptionId) {
        Customer customer = getCustomerFromUsername(username);
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));

        if (!subscription.getCustomer().getId().equals(customer.getId())) {
            throw new SecurityException("You do not have permission to cancel this subscription.");
        }

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);

        // Check for other *active* subscriptions
        boolean hasOtherActiveSubs = customer.getSubscriptions().stream()
                .anyMatch(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE && !sub.getId().equals(subscriptionId));

        if (!hasOtherActiveSubs) {
            customer.setStatus(ServiceStatus.INACTIVE);
            customerRepository.save(customer);
        }
    }

    private Customer getCustomerFromUsername(String username) {
        return customerRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer profile not found for user: " + username));
    }
}