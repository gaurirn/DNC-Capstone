package com.training.dunningcuring.audit.entity;

import com.training.dunningcuring.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

@Entity
@Table(name = "dunning_event_log")
@Getter
@Setter
@NoArgsConstructor
public class DunningEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We use FetchType.LAZY for performance
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @Column(nullable = false)
    private String actionTaken; // e.g., "THROTTLE_DATA", "CURED"

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;

    // "SYSTEM_SCHEDULER", "AGENT:susan", "ADMIN:admin"
    @Column(nullable = false)
    private String triggeredBy;

    // e.g., "Triggered by Rule ID: 1"
    private String details;

    public DunningEventLog(Customer customer, String actionTaken, String triggeredBy, String details) {
        this.customer = customer;
        this.actionTaken = actionTaken;
        this.triggeredBy = triggeredBy;
        this.details = details;
        this.eventTimestamp = LocalDateTime.now();
    }
}
