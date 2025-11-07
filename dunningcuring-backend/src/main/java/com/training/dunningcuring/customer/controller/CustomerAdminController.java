package com.training.dunningcuring.customer.controller;

import com.training.dunningcuring.customer.dto.AdminCustomerDTO;
import com.training.dunningcuring.customer.dto.AdminCustomerUpdateDTO;
import com.training.dunningcuring.customer.service.CustomerService;
import com.training.dunningcuring.invoice.entity.Invoice;
import com.training.dunningcuring.invoice.service.InvoiceService; // <-- This is already imported
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/customers")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CustomerAdminController {

    private final CustomerService customerService;
    private final InvoiceService invoiceService;

    // Your constructor is correct
    public CustomerAdminController(CustomerService customerService, InvoiceService invoiceService) {
        this.customerService = customerService;
        this.invoiceService = invoiceService;
    }

    // --- (Your existing GET, PUT, DELETE methods are all correct) ---
    @GetMapping
    public ResponseEntity<List<AdminCustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminCustomerDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminCustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody AdminCustomerUpdateDTO updateDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // --- THIS IS THE FIX ---
    // Change this method to call invoiceService directly
    @GetMapping("/{id}/invoices")
    public ResponseEntity<List<Invoice>> getCustomerInvoices(@PathVariable Long id) {
        // This call is transactional and safe
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomerId(id));
    }
    // --- END OF FIX ---

    // This method is also correct and should stay
    @PutMapping("/invoices/{invoiceId}/due-date")
    public ResponseEntity<Invoice> updateInvoiceDueDate(
            @PathVariable Long invoiceId,
            @RequestBody Map<String, String> body) {

        LocalDate newDueDate = LocalDate.parse(body.get("dueDate"));
        Invoice updatedInvoice = invoiceService.updateInvoiceDueDate(invoiceId, newDueDate);
        return ResponseEntity.ok(updatedInvoice);
    }
}