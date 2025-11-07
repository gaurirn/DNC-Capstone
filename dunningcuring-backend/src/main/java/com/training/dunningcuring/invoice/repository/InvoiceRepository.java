package com.training.dunningcuring.invoice.repository;

import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.invoice.entity.Invoice;
import com.training.dunningcuring.invoice.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Find all invoices that are due
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);

    // Find all unpaid invoices for a specific customer
    List<Invoice> findByCustomerAndStatusIn(Customer customer, List<InvoiceStatus> statuses);

    // Find all invoices for a customer (for their history)
    List<Invoice> findByCustomerOrderByIssueDateDesc(Customer customer);
    List<Invoice> findByCustomerId(Long customerId);
}