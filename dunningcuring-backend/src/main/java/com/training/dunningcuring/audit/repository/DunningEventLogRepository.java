package com.training.dunningcuring.audit.repository;

import com.training.dunningcuring.audit.entity.DunningEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DunningEventLogRepository extends JpaRepository<DunningEventLog, Long> {

    // Your existing method for the Admin Panel
    @Query("SELECT d FROM DunningEventLog d JOIN FETCH d.customer c ORDER BY d.eventTimestamp DESC")
    List<DunningEventLog> findAllWithCustomerByOrderByEventTimestampDesc();

    // --- ADD THIS NEW METHOD ---
    // Finds all logs for a customer by their username, sorted newest first
    List<DunningEventLog> findByCustomerUserUsernameOrderByEventTimestampDesc(String username);
    // --- END OF ADDITION ---
}