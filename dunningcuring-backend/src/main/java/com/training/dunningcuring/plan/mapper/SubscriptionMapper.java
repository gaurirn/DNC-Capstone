package com.training.dunningcuring.plan.mapper; // <-- CORRECTED PACKAGE

import com.training.dunningcuring.plan.entity.Plan;
import com.training.dunningcuring.plan.dto.PlanSummaryDTO;
import com.training.dunningcuring.plan.dto.SubscriptionDTO; // <-- CORRECTED IMPORT
import com.training.dunningcuring.plan.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    PlanSummaryDTO planToPlanSummaryDto(Plan plan);

    @Mapping(source = "plan", target = "plan")
    @Mapping(source = "activationDate", target = "startDate")
    SubscriptionDTO toDto(Subscription subscription);

    List<SubscriptionDTO> toDtoList(List<Subscription> subscriptions);
}