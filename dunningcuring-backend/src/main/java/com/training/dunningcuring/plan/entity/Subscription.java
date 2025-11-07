package com.training.dunningcuring.plan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.training.dunningcuring.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference // Prevents recursion
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER) // We want to load plan details
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    private LocalDate activationDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    public Subscription(Customer customer, Plan plan) {
        this.customer = customer;
        this.plan = plan;
        this.activationDate = LocalDate.now();
        this.status = SubscriptionStatus.ACTIVE;
    }
}