package com.training.dunningcuring.audit.service;

import com.training.dunningcuring.audit.entity.DunningEventLog;
import com.training.dunningcuring.audit.repository.DunningEventLogRepository;
import com.training.dunningcuring.customer.entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final DunningEventLogRepository logRepository;

    public AuditLogService(DunningEventLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(Customer customer, String action, String triggeredBy, String details) {
        DunningEventLog logEvent = new DunningEventLog(customer, action, triggeredBy, details);
        logRepository.save(logEvent);
    }
}
