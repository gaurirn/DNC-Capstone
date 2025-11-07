package com.training.dunningcuring.plan.entity;

import com.training.dunningcuring.customer.entity.CustomerSegment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String planName; // e.g., "Unlimited 5G"

    private String description; // e.g., "Premium 5G" or "100 mbps"

    @Column(nullable = false)
    private BigDecimal price; // e.g., 299

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type; // MOBILE or BROADBAND

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerSegment segment; // POSTPAID, PREPAID, or ALL

    private boolean isActive = true; // Is this plan available for new signups?

    // Data limit in MB. 0 means unlimited.
    private double dataLimitMb = 0.0;

    /**
     * Constructor for basic plans (no data limit)
     */
    public Plan(String planName, String description, BigDecimal price, ServiceType type, CustomerSegment segment) {
        this.planName = planName;
        this.description = description;
        this.price = price;
        this.type = type;
        this.segment = segment;
        this.dataLimitMb = 0.0; // Default to 0
    }

    /**
     * Full constructor including data limit
     */
    public Plan(String planName, String description, BigDecimal price, ServiceType type, CustomerSegment segment, double dataLimitMb) {
        this.planName = planName;
        this.description = description;
        this.price = price;
        this.type = type;
        this.segment = segment;
        this.dataLimitMb = dataLimitMb;
    }
}