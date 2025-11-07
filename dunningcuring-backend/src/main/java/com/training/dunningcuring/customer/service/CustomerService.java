package com.training.dunningcuring.customer.service;

import com.training.dunningcuring.auth.entity.User;
import com.training.dunningcuring.auth.repository.UserRepository;
import com.training.dunningcuring.customer.dto.AdminCustomerDTO;
import com.training.dunningcuring.customer.dto.AdminCustomerUpdateDTO;
import com.training.dunningcuring.customer.dto.CustomerStatusDTO;
import com.training.dunningcuring.customer.dto.ProfileUpdateDTO;
import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.mapper.CustomerMapper;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.exception.CustomerNotFoundException;
import com.training.dunningcuring.exception.EmailInUseException;
import com.training.dunningcuring.exception.ResourceNotFoundException;
import com.training.dunningcuring.invoice.entity.Invoice;
import com.training.dunningcuring.invoice.entity.InvoiceStatus;
import com.training.dunningcuring.invoice.mapper.InvoiceMapper;
import com.training.dunningcuring.invoice.repository.InvoiceRepository;
import com.training.dunningcuring.plan.entity.Subscription;
import com.training.dunningcuring.plan.entity.SubscriptionStatus;
import com.training.dunningcuring.plan.mapper.SubscriptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final SubscriptionMapper subscriptionMapper;

    public CustomerService(CustomerRepository customerRepository,
                           UserRepository userRepository,
                           CustomerMapper customerMapper,
                           InvoiceRepository invoiceRepository,
                           InvoiceMapper invoiceMapper,
                           SubscriptionMapper subscriptionMapper) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.customerMapper = customerMapper;
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.subscriptionMapper = subscriptionMapper;
    }

    @Transactional(readOnly = true)
    public AdminCustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer", "id", id);
                });
        return customerMapper.toAdminCustomerDTO(customer);
    }

    @Transactional(readOnly = true)
    public List<AdminCustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toAdminCustomerDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminCustomerDTO updateCustomer(Long id, AdminCustomerUpdateDTO updateDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer", "id", id);
                });

        if (StringUtils.hasText(updateDTO.getFirstName())) {
            customer.setFirstName(updateDTO.getFirstName());
        }
        if (StringUtils.hasText(updateDTO.getLastName())) {
            customer.setLastName(updateDTO.getLastName());
        }
        if (StringUtils.hasText(updateDTO.getPhone())) {
            customer.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getSegment() != null) {
            customer.setSegment(updateDTO.getSegment());
        }
        if (updateDTO.getStatus() != null) {
            customer.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getDueDate() != null) {
            customer.setDueDate(updateDTO.getDueDate());
        }
        if (StringUtils.hasText(updateDTO.getEmail())) {
            customer.setEmail(updateDTO.getEmail());
            User user = customer.getUser();
            if (user != null && !user.getUsername().equals(updateDTO.getEmail())) {
                user.setUsername(updateDTO.getEmail());
                userRepository.save(user);
            }
        }
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toAdminCustomerDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer", "id", id);
                });
        User user = customer.getUser();
        if (user != null) {
            userRepository.delete(user);
        } else {
            customerRepository.delete(customer);
        }
    }

    @Transactional(readOnly = true)
    public CustomerStatusDTO getCustomerStatus(String username) {
        Customer customer = customerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.error("Customer profile not found for user: {}", username);
                    return new CustomerNotFoundException("Customer profile not found for user: " + username);
                });

        CustomerStatusDTO dto = new CustomerStatusDTO();
        dto.setProfile(customerMapper.toProfileDTO(customer));

        List<Subscription> activeSubs = customer.getSubscriptions().stream()
                .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE)
                .collect(Collectors.toList());
        dto.setActiveSubscriptions(subscriptionMapper.toDtoList(activeSubs));

        List<Invoice> unpaid = invoiceRepository.findByCustomerAndStatusIn(
                customer,
                List.of(InvoiceStatus.ISSUED, InvoiceStatus.OVERDUE)
        );
        dto.setUnpaidInvoices(invoiceMapper.toDtoList(unpaid));

        return dto;
    }

    @Transactional
    public AdminCustomerDTO updateCustomerProfile(String username, ProfileUpdateDTO updateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        Customer customer = user.getCustomerProfile();
        if (customer == null) {
            log.error("CustomerProfile not found for user: {}", username);
            throw new ResourceNotFoundException("CustomerProfile", "user", username);
        }

        if (StringUtils.hasText(updateDTO.getFirstName())) {
            customer.setFirstName(updateDTO.getFirstName());
        }
        if (StringUtils.hasText(updateDTO.getLastName())) {
            customer.setLastName(updateDTO.getLastName());
        }
        if (StringUtils.hasText(updateDTO.getPhone())) {
            customer.setPhone(updateDTO.getPhone());
        }
        if (StringUtils.hasText(updateDTO.getEmail())) {
            if (!user.getUsername().equals(updateDTO.getEmail()) && userRepository.existsByUsername(updateDTO.getEmail())) {
                log.error("Attempted to update to an email that is already in use: {}", updateDTO.getEmail());
                throw new EmailInUseException("Error: Email is already in use!");
            }
            customer.setEmail(updateDTO.getEmail());
            user.setUsername(updateDTO.getEmail());
            userRepository.save(user);
        }
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toAdminCustomerDTO(updatedCustomer);
    }
}
