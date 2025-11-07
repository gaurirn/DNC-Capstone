package com.training.dunningcuring.customer.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

// All fields are optional, just like our Admin update
@Getter
@Setter
public class ProfileUpdateDTO {

    private String firstName;
    private String lastName;
    private String phone;

    @Email // If they provide an email, it must be valid
    private String email;
}