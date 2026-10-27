package com.thedndpub.data.services.rule;

import com.thedndpub.data.dto.rule.RuleRecord;
import com.thedndpub.data.repositories.rule.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<RuleRecord> getAll() {
        return ruleRepository.findAll();
    }

    public Optional<RuleRecord> getById(String id) {
        return ruleRepository.findAll().stream()
                .filter(rule -> rule.id().equals(id))
                .findFirst();
    }

    public List<RuleRecord> getByName(String name) {
        return ruleRepository.findAll().stream()
                .filter(rule -> rule.name().equals(name))
                .toList();
    }
}
