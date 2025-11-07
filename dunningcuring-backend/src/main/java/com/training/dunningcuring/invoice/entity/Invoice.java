package com.training.dunningcuring.invoice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL, // If we delete an invoice, delete its line items
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

//    // This links the payment that *paid* this invoice
//    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL)
//    private Payment payment;

    public Invoice(Customer customer, LocalDate issueDate, LocalDate dueDate) {
        this.customer = customer;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = InvoiceStatus.ISSUED;
    }

    // Helper method
    public void addLineItem(InvoiceLineItem item) {
        lineItems.add(item);
        item.setInvoice(this);
        this.totalAmount = this.totalAmount.add(item.getAmount());
    }
}