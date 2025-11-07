package com.training.dunningcuring.customer.mapper;

import com.training.dunningcuring.customer.dto.AdminCustomerDTO;
import com.training.dunningcuring.customer.dto.CustomerProfileDTO; // <-- IMPORT
import com.training.dunningcuring.customer.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // --- (This is your existing method for the Admin Panel) ---
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "amountOverdue", target = "amountOverdue")
    AdminCustomerDTO toAdminCustomerDTO(Customer customer);

    List<AdminCustomerDTO> toAdminCustomerDTOList(List<Customer> customers);

    // --- ADD THIS NEW METHOD ---
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "amountOverdue", target = "amountOverdue")
    CustomerProfileDTO toProfileDTO(Customer customer);
    // --- END OF ADDITION ---
}