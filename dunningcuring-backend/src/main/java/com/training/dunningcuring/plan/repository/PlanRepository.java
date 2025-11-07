package com.training.dunningcuring.plan.repository;

import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByPlanName(String planName);

    // --- ADD THIS METHOD ---
    List<Plan> findByIsActiveTrueAndSegment(CustomerSegment segment);
    // --- END OF ADDITION ---
}