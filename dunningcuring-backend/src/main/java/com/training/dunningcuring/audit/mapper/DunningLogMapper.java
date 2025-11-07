package com.training.dunningcuring.audit.mapper;

import com.training.dunningcuring.audit.dto.DunningLogDTO;
import com.training.dunningcuring.audit.entity.DunningEventLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DunningLogMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.firstName", target = "customerName")
    @Mapping(source = "actionTaken", target = "eventType") // <-- THIS IS THE FIX
    DunningLogDTO toDto(DunningEventLog log);

    List<DunningLogDTO> toDtoList(List<DunningEventLog> logs);
}