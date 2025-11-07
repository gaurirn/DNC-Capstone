package com.training.dunningcuring.invoice.dto;

import com.training.dunningcuring.invoice.entity.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
}