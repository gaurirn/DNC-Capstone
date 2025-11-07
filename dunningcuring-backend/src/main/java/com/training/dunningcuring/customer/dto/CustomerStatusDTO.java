package com.training.dunningcuring.customer.dto;

import com.training.dunningcuring.invoice.dto.InvoiceDTO;
import com.training.dunningcuring.plan.dto.SubscriptionDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor // <-- ADD THIS
public class CustomerStatusDTO {

    private CustomerProfileDTO profile;
    private List<SubscriptionDTO> activeSubscriptions;
    private List<InvoiceDTO> unpaidInvoices;

    // --- ALL LOGIC AND CONSTRUCTOR REMOVED ---
    // The service will set these fields manually.
}