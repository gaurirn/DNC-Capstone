package com.training.dunningcuring.auth.dto;


import com.training.dunningcuring.customer.entity.CustomerSegment;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username; // This will be their email

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 10, max = 15)
    private String phone;

    @NotBlank
    private String firstName;

    private String lastName;

    @NotNull(message = "Customer segment is required")
    private CustomerSegment segment; // e.g., POSTPAID or PREPAID
}
