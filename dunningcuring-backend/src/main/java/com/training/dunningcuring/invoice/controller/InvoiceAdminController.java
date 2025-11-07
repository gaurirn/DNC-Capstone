package com.training.dunningcuring.invoice.controller;

import com.training.dunningcuring.invoice.entity.Invoice;
import com.training.dunningcuring.invoice.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/invoices") // Note the new base path
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class InvoiceAdminController {

    private final InvoiceService invoiceService;

    public InvoiceAdminController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * This is the new endpoint you will use to make a customer overdue.
     */
    @PutMapping("/{invoiceId}/due-date")
    public ResponseEntity<Invoice> updateInvoiceDueDate(
            @PathVariable Long invoiceId,
            @RequestBody Map<String, String> body) {

        // We get the date from a simple JSON body like: {"dueDate": "2025-10-27"}
        LocalDate newDueDate = LocalDate.parse(body.get("dueDate"));
        Invoice updatedInvoice = invoiceService.updateInvoiceDueDate(invoiceId, newDueDate);
        return ResponseEntity.ok(updatedInvoice);
    }
}