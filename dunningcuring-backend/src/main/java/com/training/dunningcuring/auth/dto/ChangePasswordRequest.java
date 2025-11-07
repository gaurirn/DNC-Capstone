package com.training.dunningcuring.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 40, message = "New password must be between 6 and 40 characters")
    private String newPassword;
}
