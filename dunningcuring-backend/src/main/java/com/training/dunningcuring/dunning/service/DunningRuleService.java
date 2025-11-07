package com.training.dunningcuring.dunning.service;

import com.training.dunningcuring.dunning.dto.DunningRuleDTO;
import com.training.dunningcuring.dunning.entity.DunningRule;
import com.training.dunningcuring.dunning.mapper.DunningRuleMapper;
import com.training.dunningcuring.dunning.repository.DunningRuleRepository;
import com.training.dunningcuring.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DunningRuleService {

    private final DunningRuleRepository dunningRuleRepository;
    private final DunningRuleMapper dunningRuleMapper;

    public DunningRuleService(DunningRuleRepository dunningRuleRepository, DunningRuleMapper dunningRuleMapper) {
        this.dunningRuleRepository = dunningRuleRepository;
        this.dunningRuleMapper = dunningRuleMapper;
    }

    @Transactional(readOnly = true)
    public List<DunningRuleDTO> getAllRules() {
        List<DunningRule> rules = dunningRuleRepository.findAll();
        return dunningRuleMapper.toDtoList(rules);
    }


    @Transactional(readOnly = true)
    public DunningRuleDTO getRuleById(Long id) {
        DunningRule rule = dunningRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DunningRule", "id", id));
        return dunningRuleMapper.toDto(rule);
    }


    @Transactional
    public DunningRuleDTO createRule(@Valid DunningRuleDTO dto) {
        DunningRule rule = dunningRuleMapper.toEntity(dto);
        DunningRule savedRule = dunningRuleRepository.save(rule);
        return dunningRuleMapper.toDto(savedRule);
    }

    @Transactional
    public DunningRuleDTO updateRule(Long id, @Valid DunningRuleDTO dto) {
        DunningRule rule = dunningRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DunningRule", "id", id));

        dunningRuleMapper.updateEntityFromDto(dto, rule);
        DunningRule updatedRule = dunningRuleRepository.save(rule);
        return dunningRuleMapper.toDto(updatedRule);
    }

    @Transactional
    public void deleteRule(Long id) {
        if (!dunningRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("DunningRule", "id", id);
        }
        dunningRuleRepository.deleteById(id);
    }
}