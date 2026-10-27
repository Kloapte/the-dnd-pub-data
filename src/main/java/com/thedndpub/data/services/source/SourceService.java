package com.thedndpub.data.services.source;

import com.thedndpub.data.dto.source.Source;
import com.thedndpub.data.repositories.source.SourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SourceService {
    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    public List<Source> getAll() {
        return sourceRepository.findAll();
    }

    public Optional<Source> getById(String code) {
        return sourceRepository.findAll().stream()
                .filter(source -> source.code().equals(code))
                .findFirst();
    }

    public Optional<Source> getByName(String name) {
        return sourceRepository.findAll().stream()
                .filter(source -> source.publications().stream()
                        .anyMatch(publication -> name.equals(publication.name())))
                .findFirst();
    }
}
