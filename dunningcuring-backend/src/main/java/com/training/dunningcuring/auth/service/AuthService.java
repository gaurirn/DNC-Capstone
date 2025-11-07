package com.training.dunningcuring.auth.service;

import com.training.dunningcuring.auth.dto.JwtResponse;
import com.training.dunningcuring.auth.dto.LoginRequest;
import com.training.dunningcuring.auth.dto.SignupRequest;
import com.training.dunningcuring.auth.entity.ERole;
import com.training.dunningcuring.auth.entity.Role;
import com.training.dunningcuring.auth.entity.User;
import com.training.dunningcuring.auth.repository.RoleRepository;
import com.training.dunningcuring.auth.repository.UserRepository;
import com.training.dunningcuring.auth.security.jwt.JwtUtils;
import com.training.dunningcuring.auth.security.services.UserDetailsImpl;
import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.exception.RoleNotFoundException;
import com.training.dunningcuring.exception.UserAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles);
    }

    @Transactional
    public void registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException("Error: Role is not found."));
        roles.add(userRole);


        user.setRoles(roles);

        // --- Create a new Customer profile and link it ---
        Customer customerProfile = new Customer();
        customerProfile.setFirstName(signUpRequest.getFirstName());
        customerProfile.setLastName(signUpRequest.getLastName());
        customerProfile.setEmail(signUpRequest.getEmail());
        customerProfile.setPhone(signUpRequest.getPhone());

        // --- Set defaults from the signup form ---
        customerProfile.setSegment(signUpRequest.getSegment());
        customerProfile.setStatus(ServiceStatus.INACTIVE);
        customerProfile.setBalance(BigDecimal.ZERO);
        customerProfile.setAmountOverdue(BigDecimal.ZERO);
        customerProfile.setOverdueDays(0);

        // Link the user to the profile
        customerProfile.setUser(user);
        // Link the profile to the user
        user.setCustomerProfile(customerProfile);

        userRepository.save(user);
    }
}
