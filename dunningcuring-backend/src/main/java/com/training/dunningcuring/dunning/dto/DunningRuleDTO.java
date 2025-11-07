package com.training.dunningcuring.dunning.dto;

import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.dunning.entity.DunningAction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DunningRuleDTO {
    private Long id;

    @NotNull(message = "Rule name is required")
    private String ruleName;

    @NotNull(message = "Action is required")
    private DunningAction actionToTake;

    @NotNull(message = "Segment is required")
    private CustomerSegment targetSegment;

    @Min(value = 0, message = "Min days must be 0 or greater")
    private int minOverdueDays;

    @Min(value = 1, message = "Max days must be 1 or greater")
    private int maxOverdueDays;

    private boolean active = true;
}