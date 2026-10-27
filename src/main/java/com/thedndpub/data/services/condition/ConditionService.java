package com.thedndpub.data.services.condition;

import com.thedndpub.data.dto.condition.ConditionRecord;
import com.thedndpub.data.repositories.condition.ConditionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConditionService {
    private final ConditionRepository conditionRepository;

    public ConditionService(ConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    public List<ConditionRecord> getAll() {
        return conditionRepository.findAll();
    }

    public Optional<ConditionRecord> getById(String id) {
        return conditionRepository.findAll().stream()
                .filter(condition -> condition.id().equals(id))
                .findFirst();
    }

    public List<ConditionRecord> getByName(String name) {
        return conditionRepository.findAll().stream()
                .filter(condition -> condition.name().equals(name))
                .toList();
    }
}
