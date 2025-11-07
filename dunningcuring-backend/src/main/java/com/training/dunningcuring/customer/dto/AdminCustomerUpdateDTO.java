package com.training.dunningcuring.customer.dto;

import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
// Import @Email but remove @NotBlank and @NotNull
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class AdminCustomerUpdateDTO {

    // We keep @Email to ensure if they *do* send an email, it's valid.
    @Email
    private String email;

    // All other validation is removed to make them optional
    private String phone;
    private String firstName;
    private String lastName;
    private CustomerSegment segment;
    private ServiceStatus status;
    private LocalDate dueDate;
}