package com.training.dunningcuring.audit.service;

import com.training.dunningcuring.audit.dto.DunningLogDTO;
import com.training.dunningcuring.audit.entity.DunningEventLog;
import com.training.dunningcuring.audit.mapper.DunningLogMapper;
import com.training.dunningcuring.audit.repository.DunningEventLogRepository;
import com.training.dunningcuring.customer.entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DunningLogService {

    private final DunningEventLogRepository dunningLogRepository;
    private final DunningLogMapper dunningLogMapper;

    public DunningLogService(DunningEventLogRepository dunningLogRepository, DunningLogMapper dunningLogMapper) {
        this.dunningLogRepository = dunningLogRepository;
        this.dunningLogMapper = dunningLogMapper;
    }


    @Transactional(readOnly = true)
    public List<DunningLogDTO> getAllDunningLogs() {
        return dunningLogMapper.toDtoList(dunningLogRepository.findAllWithCustomerByOrderByEventTimestampDesc());
    }


    @Transactional
    public void logEvent(Customer customer, String eventType, String source, String message) {

        DunningEventLog log = new DunningEventLog(
                customer,
                eventType,
                source,
                message
        );
        dunningLogRepository.save(log);
    }


    @Transactional(readOnly = true)
    public List<DunningLogDTO> getLogsForCustomer(String username) {

        List<DunningEventLog> logs = dunningLogRepository.findByCustomerUserUsernameOrderByEventTimestampDesc(username);
        return dunningLogMapper.toDtoList(logs);
    }

}