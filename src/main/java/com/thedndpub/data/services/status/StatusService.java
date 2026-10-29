package com.thedndpub.data.services.status;

import com.thedndpub.data.dto.status.StatusRecord;
import com.thedndpub.data.repositories.status.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<StatusRecord> getAll() {
        return statusRepository.findAll();
    }

    public Optional<StatusRecord> getById(String id) {
        return statusRepository.findAll().stream()
                .filter(status -> status.id().equals(id))
                .findFirst();
    }

    public List<StatusRecord> getByName(String name) {
        return statusRepository.findAll().stream()
                .filter(status -> status.name().equals(name))
                .toList();
    }
}
