package com.training.dunningcuring.payment.dto;

import lombok.Getter;
import lombok.Setter;

// A simple DTO, we'll just mock a "Top-up" of 1GB
@Getter
@Setter
public class TopUpRequestDTO {
    private String topUpPackage = "1GB_BOOSTER";
}