package com.training.dunningcuring.invoice.mapper;

import com.training.dunningcuring.invoice.dto.InvoiceDTO;
import com.training.dunningcuring.invoice.entity.Invoice;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceDTO toDto(Invoice invoice);
    List<InvoiceDTO> toDtoList(List<Invoice> invoices);
}