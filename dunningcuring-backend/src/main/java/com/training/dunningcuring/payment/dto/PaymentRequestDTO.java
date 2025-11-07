package com.training.dunningcuring.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDTO {
    @NotNull
    @DecimalMin(value = "0.01", message = "Payment amount must be positive")
    private BigDecimal amount;
}
