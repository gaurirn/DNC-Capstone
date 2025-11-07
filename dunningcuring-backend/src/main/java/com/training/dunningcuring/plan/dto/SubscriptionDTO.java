package com.training.dunningcuring.plan.dto; // <-- CORRECTED PACKAGE

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class SubscriptionDTO {
    private Long id;
    private LocalDate startDate;
    private PlanSummaryDTO plan;
}