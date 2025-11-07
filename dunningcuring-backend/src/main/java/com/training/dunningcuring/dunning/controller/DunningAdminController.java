package com.training.dunningcuring.dunning.controller;

import com.training.dunningcuring.dunning.dto.DunningRuleDTO;
import com.training.dunningcuring.dunning.service.DunningRuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600) // Good, this is commented out
@RestController
@RequestMapping("/api/admin/rules")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class DunningAdminController {

    private final DunningRuleService ruleService;

    public DunningAdminController(DunningRuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping
    public ResponseEntity<DunningRuleDTO> createRule(@Valid @RequestBody DunningRuleDTO dto) {
        DunningRuleDTO createdRule = ruleService.createRule(dto);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DunningRuleDTO>> getAllRules() {
        List<DunningRuleDTO> rules = ruleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DunningRuleDTO> getRuleById(@PathVariable Long id) {
        DunningRuleDTO rule = ruleService.getRuleById(id);
        return ResponseEntity.ok(rule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DunningRuleDTO> updateRule(@PathVariable Long id, @Valid @RequestBody DunningRuleDTO dto) {
        DunningRuleDTO updatedRule = ruleService.updateRule(id, dto);
        return ResponseEntity.ok(updatedRule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}