package com.thedndpub.data.services.sense;

import com.thedndpub.data.dto.sense.SenseRecord;
import com.thedndpub.data.repositories.sense.SenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SenseService {
    private final SenseRepository senseRepository;

    public SenseService(SenseRepository senseRepository) {
        this.senseRepository = senseRepository;
    }

    public List<SenseRecord> getAll() {
        return senseRepository.findAll();
    }

    public Optional<SenseRecord> getById(String id) {
        return senseRepository.findAll().stream()
                .filter(sense -> sense.id().equals(id))
                .findFirst();
    }

    public List<SenseRecord> getByName(String name) {
        return senseRepository.findAll().stream()
                .filter(sense -> sense.name().equals(name))
                .toList();
    }
}
