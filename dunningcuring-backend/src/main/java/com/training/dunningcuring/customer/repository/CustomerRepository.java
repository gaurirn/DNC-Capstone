package com.training.dunningcuring.customer.repository;

import com.training.dunningcuring.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
    long countByStatus(ServiceStatus status);

    List<Customer> findTop5ByStatusInAndAmountOverdueGreaterThanOrderByAmountOverdueDesc(
            List<ServiceStatus> statuses,
            BigDecimal amount
    );


    Optional<Customer> findByUserUsername(String username);

}