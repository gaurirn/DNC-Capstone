package com.training.dunningcuring.payment.service;

import com.training.dunningcuring.payment.dto.PaymentDTO;
import com.training.dunningcuring.payment.entity.Payment;
import com.training.dunningcuring.payment.entity.PaymentType;
import com.training.dunningcuring.payment.mapper.PaymentMapper;
import com.training.dunningcuring.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional // <-- Add this to the class
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        // --- THIS IS THE FIX ---
        return paymentMapper.toDtoList(paymentRepository.findAllWithCustomerOrderByPaymentDateDesc());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getCuredPayments() {
        // --- THIS IS THE FIX ---
        return paymentMapper.toDtoList(
                paymentRepository.findByTypeWithCustomerOrderByPaymentDateDesc(PaymentType.INVOICE_PAYMENT)
        );
    }
    // --- ADD THIS NEW METHOD ---
    // This is for the Customer Portal
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsForCustomer(String username) {
        List<Payment> payments = paymentRepository.findByCustomerUserUsernameOrderByPaymentDateDesc(username);
        return paymentMapper.toDtoList(payments);
    }
    // --- END OF ADDITION ---
}