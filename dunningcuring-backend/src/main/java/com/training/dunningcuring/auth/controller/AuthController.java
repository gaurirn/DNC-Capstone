package com.training.dunningcuring.auth.controller;

import com.training.dunningcuring.auth.dto.ChangePasswordRequest;
import com.training.dunningcuring.auth.entity.User;
import com.training.dunningcuring.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.security.Principal;
import java.util.Map;
import com.training.dunningcuring.auth.dto.JwtResponse;
import com.training.dunningcuring.auth.dto.LoginRequest;
import com.training.dunningcuring.auth.dto.SignupRequest;
import com.training.dunningcuring.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final OtpService otpService; // <-- DELETE THIS

    // --- UPDATE THE CONSTRUCTOR ---
    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        // this.otpService = otpService; // <-- DELETE THIS
    }

    // --- /login is unchanged ---
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    // --- THIS IS THE REVERTED /signup ENDPOINT ---
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (!signUpRequest.getUsername().equals(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Username must be the same as email."));
        }

        try {
            authService.registerUser(signUpRequest);
            return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- DELETE THE /verify ENDPOINT ---
    // @PostMapping("/verify") ...

    /// Change Password
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(Principal principal,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        String username = principal.getName();

        // Find the user by username (current authenticated user)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the old password is correct
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Incorrect old password."));
        }

        // Check if the new password is the same as the old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: New password cannot be the same as the old password."));
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword())); // Encode new password
        userRepository.save(user);

        // Return success response
        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    }
}