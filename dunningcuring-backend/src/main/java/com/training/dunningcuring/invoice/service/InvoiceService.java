package com.training.dunningcuring.invoice.service;

import com.training.dunningcuring.exception.ResourceNotFoundException;
import com.training.dunningcuring.invoice.entity.Invoice;
import com.training.dunningcuring.invoice.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List; // <-- IMPORT

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }


    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByCustomerId(Long customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }


    @Transactional
    public Invoice updateInvoiceDueDate(Long invoiceId, LocalDate newDueDate) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        invoice.setDueDate(newDueDate);
        return invoiceRepository.save(invoice);
    }
}