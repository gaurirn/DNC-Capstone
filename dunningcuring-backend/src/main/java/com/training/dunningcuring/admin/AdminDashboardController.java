package com.training.dunningcuring.admin;

import com.training.dunningcuring.audit.dto.DunningLogDTO;
import com.training.dunningcuring.audit.service.DunningLogService;
import com.training.dunningcuring.billing.service.BillingService;
import com.training.dunningcuring.billing.service.UsageService;
import com.training.dunningcuring.customer.dto.AdminCustomerDTO;
import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.customer.mapper.CustomerMapper;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.dunning.service.DunningEngineService;
import com.training.dunningcuring.payment.dto.PaymentDTO;
import com.training.dunningcuring.payment.entity.Payment;
import com.training.dunningcuring.payment.entity.PaymentType;
import com.training.dunningcuring.payment.mapper.PaymentMapper;
import com.training.dunningcuring.payment.repository.PaymentRepository;
import com.training.dunningcuring.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminDashboardController {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final BillingService billingService;
    private final UsageService usageService;
    private final DunningEngineService dunningEngineService;
    private final CustomerMapper customerMapper;
    private final DunningLogService dunningLogService;
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public AdminDashboardController(CustomerRepository customerRepository,
                                    PaymentRepository paymentRepository,
                                    BillingService billingService,
                                    UsageService usageService,
                                    DunningEngineService dunningEngineService,
                                    CustomerMapper customerMapper,
                                    PaymentService paymentService,
                                    PaymentMapper paymentMapper,
                                    DunningLogService dunningLogService) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.billingService = billingService;
        this.usageService = usageService;
        this.dunningEngineService = dunningEngineService;
        this.customerMapper = customerMapper;
        this.dunningLogService = dunningLogService;
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    // --- THIS IS THE FIX ---
    // Changed "/payments-all" to "/payments"
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        // This endpoint is for the "Payments Overview" page
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    // --- END OF FIX ---

    @GetMapping("/cured-payments")
    public ResponseEntity<List<PaymentDTO>> getCuredPayments() {
        // This endpoint is for the "Curing" page
        return ResponseEntity.ok(paymentService.getCuredPayments());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<DunningLogDTO>> getAllLogs() {
        return ResponseEntity.ok(dunningLogService.getAllDunningLogs());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        long totalCustomers = customerRepository.count();
        BigDecimal totalRevenue = paymentRepository.findByTypeWithCustomerOrderByPaymentDateDesc(PaymentType.TOP_UP).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long throttledAccounts = customerRepository.countByStatus(ServiceStatus.THROTTLED);
        long blockedAccounts = customerRepository.countByStatus(ServiceStatus.BLOCKED);
        long inactiveAccounts = customerRepository.countByStatus(ServiceStatus.INACTIVE);

        Map<String, Object> stats = Map.of(
                "totalCustomers", totalCustomers,
                "totalRecoveredRevenue", totalRevenue,
                "accountsThrottled", throttledAccounts,
                "accountsBlocked", blockedAccounts,
                "accountsInactive", inactiveAccounts
        );
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/top-risk-accounts")
    public ResponseEntity<List<AdminCustomerDTO>> getTopRiskAccounts() {
        List<Customer> topCustomers = customerRepository
                .findTop5ByStatusInAndAmountOverdueGreaterThanOrderByAmountOverdueDesc(
                        List.of(ServiceStatus.THROTTLED, ServiceStatus.BLOCKED),
                        BigDecimal.ZERO
                );

        List<AdminCustomerDTO> dtos = topCustomers.stream()
                .map(customerMapper::toAdminCustomerDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ... (All POST trigger endpoints are unchanged) ...
    @PostMapping("/trigger/billing-cycle")
    public ResponseEntity<Map<String, String>> triggerBillingCycle() {
        billingService.runBillingCycleUpdate();
        return ResponseEntity.ok(Map.of("message", "Customer status update triggered successfully."));
    }

    @PostMapping("/trigger/invoice-creation")
    public ResponseEntity<Map<String, String>> triggerInvoiceCreation() {
        billingService.createMonthlyInvoices();
        return ResponseEntity.ok(Map.of("message", "Forced invoice creation triggered successfully."));
    }

    @PostMapping("/trigger/usage")
    public ResponseEntity<Map<String, String>> triggerUsageSimulation() {
        usageService.simulateDataUsage();
        return ResponseEntity.ok(Map.of("message", "Prepaid usage simulation triggered successfully."));
    }

    @PostMapping("/trigger/dunning")
    public ResponseEntity<Map<String, String>> triggerDunningCycle() {
        dunningEngineService.runDunningCycle();
        return ResponseEntity.ok(Map.of("message", "Dunning engine cycle triggered successfully."));
    }
}