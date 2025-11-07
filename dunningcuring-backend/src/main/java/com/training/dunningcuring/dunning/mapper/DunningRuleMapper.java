package com.training.dunningcuring.dunning.mapper;

import com.training.dunningcuring.dunning.dto.DunningRuleDTO;
import com.training.dunningcuring.dunning.entity.DunningRule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DunningRuleMapper {

    DunningRuleDTO toDto(DunningRule entity);

    List<DunningRuleDTO> toDtoList(List<DunningRule> entities);

    DunningRule toEntity(DunningRuleDTO dto);

    void updateEntityFromDto(DunningRuleDTO dto, @MappingTarget DunningRule entity);
}