package com.thedndpub.data.services.action;

import com.thedndpub.data.dto.action.ActionRecord;
import com.thedndpub.data.repositories.action.ActionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionService {
    private final ActionRepository actionRepository;

    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    public List<ActionRecord> getAll() {
        return actionRepository.findAll();
    }

    public Optional<ActionRecord> getById(String id) {
        return actionRepository.findAll().stream()
                .filter(action -> action.id().equals(id))
                .findFirst();
    }

    public List<ActionRecord> getByName(String name) {
        return actionRepository.findAll().stream()
                .filter(action -> action.name().equals(name))
                .toList();
    }
}
