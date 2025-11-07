package com.training.dunningcuring.customer.dto;

import com.training.dunningcuring.customer.entity.ServiceStatus;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerProfileDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private ServiceStatus status;
    private BigDecimal balance;
    private BigDecimal amountOverdue;
    private LocalDate dueDate;
}