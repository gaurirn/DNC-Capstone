package com.training.dunningcuring.payment.mapper;

import com.training.dunningcuring.payment.dto.PaymentDTO;
import com.training.dunningcuring.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.firstName", target = "customerName") // This will just use firstName
    PaymentDTO toDto(Payment payment);

    List<PaymentDTO> toDtoList(List<Payment> payments);
}