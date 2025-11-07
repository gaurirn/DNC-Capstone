package com.training.dunningcuring.dunning.entity;

import com.training.dunningcuring.customer.entity.CustomerSegment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dunning_rules")
@Getter
@Setter
public class DunningRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String ruleName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DunningAction actionToTake;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CustomerSegment targetSegment;

    @NotNull
    @Min(0)
    private int minOverdueDays;

    // --- ADD THIS FIELD ---
    @NotNull
    @Min(0)
    private int maxOverdueDays = 999; // Default to 999
    // --- END OF ADDITION ---

    private boolean active = true;
}