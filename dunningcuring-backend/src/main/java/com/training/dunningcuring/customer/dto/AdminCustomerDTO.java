package com.training.dunningcuring.customer.dto;

import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
// import java.util.List; // <-- REMOVE THIS
// import com.training.dunningcuring.audit.entity.DunningEventLog; // <-- REMOVE THIS
// import com.training.dunningcuring.payment.entity.Payment; // <-- REMOVE THIS

@Getter
@Setter
public class AdminCustomerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private CustomerSegment segment;
    private ServiceStatus status;
    private BigDecimal amountOverdue;
    private int overdueDays;
    private LocalDate dueDate;
    private BigDecimal balance;
}