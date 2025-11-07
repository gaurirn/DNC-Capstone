package com.training.dunningcuring.audit.dto;

import com.training.dunningcuring.dunning.entity.DunningAction;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class DunningLogDTO {

    private Long id;
    private LocalDateTime eventTimestamp;

    // --- THIS IS THE FIX ---
    private String eventType; // This was DunningAction
    // --- END OF FIX ---

    private String details;
    private Long customerId;
    private String customerName;
}