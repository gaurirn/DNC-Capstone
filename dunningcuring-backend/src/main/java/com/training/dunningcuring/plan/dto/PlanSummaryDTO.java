package com.training.dunningcuring.plan.dto;

import com.training.dunningcuring.plan.entity.ServiceType;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PlanSummaryDTO {
    private String planName;
    private ServiceType serviceType;
    private BigDecimal price;
}