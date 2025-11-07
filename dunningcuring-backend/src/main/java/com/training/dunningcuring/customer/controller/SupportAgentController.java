///*
//package com.training.dunningcuring.customer.controller;
//
//import com.training.dunningcuring.customer.dto.AdminCustomerDTO;
//import com.training.dunningcuring.customer.entity.Customer;
//import com.training.dunningcuring.customer.repository.CustomerRepository;
//import com.training.dunningcuring.customer.service.CustomerService;
//import com.training.dunningcuring.exception.ResourceNotFoundException;
//import com.training.dunningcuring.payment.dto.PaymentRequestDTO;
//import com.training.dunningcuring.payment.service.CuringService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//
////@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
//@RequestMapping("/api/support")
//@PreAuthorize("hasRole('ROLE_SUPPORT_AGENT')")
//public class SupportAgentController {
//
//    private final CustomerService customerService;
//    private final CustomerRepository customerRepository;
//    private final CuringService curingService;
//
//    public SupportAgentController(CustomerService customerService,
//                                  CustomerRepository customerRepository,
//                                  CuringService curingService) {
//        this.customerService = customerService;
//        this.customerRepository = customerRepository;
//        this.curingService = curingService;
//    }
//
//    /**
//     * Endpoint for support agent to view all customers (read-only)
//     */
//    @GetMapping("/customers")
//    public ResponseEntity<List<AdminCustomerDTO>> getAllCustomers() {
//        List<AdminCustomerDTO> customers = customerService.getAllCustomers();
//        return ResponseEntity.ok(customers);
//    }
//
//    /**
//     * Endpoint for support agent to view a single customer (read-only)
//     */
//    @GetMapping("/customers/{id}")
//    public ResponseEntity<AdminCustomerDTO> getCustomerById(@PathVariable Long id) {
//        AdminCustomerDTO customer = customerService.getCustomerById(id);
//        return ResponseEntity.ok(customer);
//    }
//
//    /**
//     * --- THIS IS THE UPDATED METHOD ---
//     * Endpoint for support agent to manually log a payment.
//     * This adds to the customer's balance and then attempts to pay their bill.
//     */
//    @PostMapping("/customers/{id}/manual-payment")
//    public ResponseEntity<String> logManualPayment(@PathVariable Long id,
//                                                   @Valid @RequestBody PaymentRequestDTO paymentDTO) {
//        Customer customer = customerRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
//
//        String source = "SUPPORT_AGENT_MANUAL";
//
//        // Step 1: Add the money to the customer's balance.
//        // This also creates a Payment record.
//        curingService.addBalance(
//                customer,
//                paymentDTO.getAmount(),
//                source
//        );
//
//        // Step 2: If the customer has a bill, try to pay it with the new balance.
//        if (customer.getAmountOverdue().compareTo(BigDecimal.ZERO) > 0) {
//            try {
//                // This will now check the balance (which we just added)
//                // and pay the bill. This is the 2-argument method call.
//                curingService.processPaymentAndCure(
//                        customer,
//                        source
//                );
//            } catch (RuntimeException e) {
//                // This happens if, for example, they paid $50 but owed $199
//                // The balance was added, but the bill couldn't be paid.
//                return ResponseEntity.badRequest().body("Payment logged, but balance is still insufficient to clear debt. " + e.getMessage());
//            }
//        }
//
//        return ResponseEntity.ok("Manual payment logged. Balance updated and service is being restored.");
//    }
//}
//*/