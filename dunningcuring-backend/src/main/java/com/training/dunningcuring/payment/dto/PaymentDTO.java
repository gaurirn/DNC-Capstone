package com.training.dunningcuring.payment.dto;

import com.training.dunningcuring.payment.entity.PaymentType;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDTO {
    private Long id;
    private String customerName;
    private Long customerId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentSource;
    private PaymentType type;
}