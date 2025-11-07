package com.training.dunningcuring.customer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.training.dunningcuring.auth.entity.User;
import com.training.dunningcuring.invoice.entity.Invoice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.training.dunningcuring.audit.entity.DunningEventLog;
import java.util.ArrayList;
import java.util.List;
// --- THIS IS THE FIX ---
import com.training.dunningcuring.plan.entity.Subscription; // <-- Corrected import path
// --- END OF FIX ---
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.training.dunningcuring.payment.entity.Payment;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(unique = true, nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerSegment segment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceStatus status = ServiceStatus.INACTIVE;

    private BigDecimal amountOverdue = BigDecimal.ZERO;
    private Integer overdueDays = 0;
    private LocalDate dueDate;

    private double dataUsageMb = 0.0;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("paymentDate DESC")
    @JsonManagedReference
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("eventTimestamp DESC")
    @JsonManagedReference
    private List<DunningEventLog> eventLogs = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("issueDate DESC")
    @JsonManagedReference
    private List<Invoice> invoices = new ArrayList<>();

    // --- Helper methods ---
    public void addLogEvent(DunningEventLog logEvent) {
        eventLogs.add(logEvent);
        logEvent.setCustomer(this);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setCustomer(this);
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
        subscription.setCustomer(this);
    }

    public void addInvoice(Invoice invoice) {
        invoices.add(invoice);
        invoice.setCustomer(this);
    }
}