package com.training.dunningcuring.payment.repository;

import com.training.dunningcuring.payment.entity.Payment;
import com.training.dunningcuring.payment.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- IMPORT
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // --- THIS IS THE FIX ---
    @Query("SELECT p FROM Payment p JOIN FETCH p.customer c ORDER BY p.paymentDate DESC")
    List<Payment> findAllWithCustomerOrderByPaymentDateDesc();

    @Query("SELECT p FROM Payment p JOIN FETCH p.customer c WHERE p.type = :type ORDER BY p.paymentDate DESC")
    List<Payment> findByTypeWithCustomerOrderByPaymentDateDesc(PaymentType type);
    // --- END OF FIX ---
    List<Payment> findByCustomerUserUsernameOrderByPaymentDateDesc(String username);
}