package com.training.dunningcuring.payment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.training.dunningcuring.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private String paymentSource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    // This is your existing constructor
    public Payment(Customer customer, BigDecimal amount, String paymentSource, PaymentType type) {
        this.customer = customer;
        this.amount = amount;
        this.paymentSource = paymentSource;
        this.type = type;
        this.paymentDate = LocalDateTime.now();
    }

    // --- ADD THIS NEW, SIMPLER CONSTRUCTOR ---
    // This is the one our services will use
    public Payment(Customer customer, BigDecimal amount, PaymentType type) {
        this.customer = customer;
        this.amount = amount;
        this.type = type;
        this.paymentSource = "CUSTOMER_PORTAL"; // Set a default source
        this.paymentDate = LocalDateTime.now();
    }
    // --- END OF ADDITION ---
}