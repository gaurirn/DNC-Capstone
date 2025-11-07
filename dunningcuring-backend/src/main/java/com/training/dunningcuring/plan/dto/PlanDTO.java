package com.training.dunningcuring.plan.dto;

import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.plan.entity.ServiceType;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PlanDTO {
    private Long id;
    private String planName;
    private String description;
    private BigDecimal price;
    private ServiceType type; // Use 'type' to match your Plan entity
    private CustomerSegment segment; // Use 'segment' to match your Plan entity
    private double dataLimitMb;
}