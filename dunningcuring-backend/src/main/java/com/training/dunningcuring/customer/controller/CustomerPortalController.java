package com.training.dunningcuring.customer.controller;

import com.training.dunningcuring.auth.entity.User;
import com.training.dunningcuring.auth.repository.UserRepository;
import com.training.dunningcuring.customer.dto.AdminCustomerDTO;
import com.training.dunningcuring.customer.dto.CustomerStatusDTO;
import com.training.dunningcuring.customer.dto.ProfileUpdateDTO;
import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.mapper.CustomerMapper;
import com.training.dunningcuring.customer.service.CustomerService;
import com.training.dunningcuring.exception.ResourceNotFoundException;
import com.training.dunningcuring.payment.dto.PaymentRequestDTO;
import com.training.dunningcuring.payment.dto.TopUpRequestDTO;
import com.training.dunningcuring.payment.service.CuringService;
import com.training.dunningcuring.plan.dto.PlanDTO; // <-- IMPORT
import com.training.dunningcuring.plan.entity.Subscription; // <-- IMPORT
import com.training.dunningcuring.plan.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; // <-- IMPORT
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.training.dunningcuring.payment.dto.PaymentDTO; // <-- IMPORT
import com.training.dunningcuring.payment.service.PaymentService; // <-- IMPORT
import java.util.List; // <-- IMPORT
import com.training.dunningcuring.audit.dto.DunningLogDTO; // <-- IMPORT
import com.training.dunningcuring.audit.service.DunningLogService; // <-- IMPORT
import java.security.Principal; // <-- IMPORT
import java.util.List;
import java.security.Principal;
import java.util.List; // <-- IMPORT
import java.util.Map; // <-- IMPORT

@RestController
@RequestMapping("/api/me")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class CustomerPortalController {

    private final CustomerService customerService;
    private final CuringService curingService;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final PlanService planService;
    private final PaymentService paymentService; // <-- ADD THIS
    private final DunningLogService dunningLogService;

    public CustomerPortalController(CustomerService customerService,
                                    CuringService curingService,
                                    UserRepository userRepository,
                                    CustomerMapper customerMapper,
                                    PlanService planService,
                                    PaymentService paymentService,
                                    DunningLogService dunningLogService) {
        this.customerService = customerService;
        this.curingService = curingService;
        this.userRepository = userRepository;
        this.customerMapper = customerMapper;
        this.planService = planService;
        this.paymentService=paymentService;
        this.dunningLogService = dunningLogService;
    }

    @GetMapping("/status")
    public ResponseEntity<CustomerStatusDTO> getMyStatus(Principal principal) {
        CustomerStatusDTO status = customerService.getCustomerStatus(principal.getName());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/payment")
    public ResponseEntity<String> makePayment(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Customer customer = user.getCustomerProfile();
        if (customer == null) {
            throw new ResourceNotFoundException("CustomerProfile", "user", principal.getName());
        }
        try {
            curingService.processPaymentAndCure(customer, "CUSTOMER_PORTAL");
            return ResponseEntity.ok("Payment successful. Your outstanding balance has been cleared.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<AdminCustomerDTO> updateMyProfile(Principal principal,
                                                            @Valid @RequestBody ProfileUpdateDTO updateDTO) {
        AdminCustomerDTO updatedCustomer = customerService.updateCustomerProfile(
                principal.getName(),
                updateDTO
        );
        return ResponseEntity.ok(updatedCustomer);
    }

    @PostMapping("/top-up")
    public ResponseEntity<String> purchaseDataTopUp(Principal principal, @RequestBody TopUpRequestDTO topUpRequest) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Customer customer = user.getCustomerProfile();
        curingService.processDataTopUp(customer, "CUSTOMER_TOPUP");
        return ResponseEntity.ok("Top-up successful. Your data speed is restored.");
    }

    @PostMapping("/add-balance")
    public ResponseEntity<AdminCustomerDTO> addBalance(Principal principal,
                                                       @Valid @RequestBody PaymentRequestDTO paymentDTO) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Customer customer = user.getCustomerProfile();
        Customer updatedCustomer = curingService.addBalance(
                customer,
                paymentDTO.getAmount(),
                "CUSTOMER_PORTAL"
        );
        return ResponseEntity.ok(customerMapper.toAdminCustomerDTO(updatedCustomer));
    }

    // --- METHODS FROM PlanController (MERGED) ---

    @GetMapping("/plans")
    public ResponseEntity<List<PlanDTO>> getAvailablePlans(Principal principal) {
        List<PlanDTO> plans = planService.getAvailablePlans(principal.getName());
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<Subscription>> getMySubscriptions(Principal principal) {
        // Note: This returns the full Subscription entity. This is fine for now,
        // but we can change it to a DTO later if it causes lazy loading errors.
        List<Subscription> subscriptions = planService.getMySubscriptions(principal.getName());
        return ResponseEntity.ok(subscriptions);
    }

    // --- ADD THIS NEW ENDPOINT ---
    @GetMapping("/notifications")
    public ResponseEntity<List<DunningLogDTO>> getMyNotifications(Principal principal) {
        List<DunningLogDTO> logs = dunningLogService.getLogsForCustomer(principal.getName());
        return ResponseEntity.ok(logs);
    }
    // --- END OF ADDITION ---

    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<?> subscribeToPlan(@PathVariable Long planId, Principal principal) {
        try {
            Subscription newSubscription = planService.subscribeToPlan(principal.getName(), planId);
            // Return a simple message instead of the full entity
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Successfully subscribed to plan " + newSubscription.getPlan().getPlanName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // --- ADD THIS NEW ENDPOINT ---
    @GetMapping("/payment-history")
    public ResponseEntity<List<PaymentDTO>> getMyPaymentHistory(Principal principal) {
        List<PaymentDTO> payments = paymentService.getPaymentsForCustomer(principal.getName());
        return ResponseEntity.ok(payments);
    }
    // --- END OF ADDITION ---

    @DeleteMapping("/subscriptions/{id}")
    public ResponseEntity<String> unsubscribeFromPlan(Principal principal, @PathVariable Long id) {
        try {
            planService.cancelSubscription(principal.getName(), id);
            return ResponseEntity.ok("Subscription (ID: " + id + ") has been canceled.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}