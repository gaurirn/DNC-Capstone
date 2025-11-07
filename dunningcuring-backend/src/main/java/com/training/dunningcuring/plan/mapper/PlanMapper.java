package com.training.dunningcuring.plan.mapper;

import com.training.dunningcuring.plan.dto.PlanDTO;
import com.training.dunningcuring.plan.entity.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    @Mapping(source = "type", target = "type") // Map plan.type to planDTO.type
    @Mapping(source = "segment", target = "segment") // Map plan.segment to planDTO.segment
    PlanDTO toDto(Plan plan);

    List<PlanDTO> toDtoList(List<Plan> plans);
}