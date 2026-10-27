package com.thedndpub.data.services.language;

import com.thedndpub.data.dto.language.LanguageRecord;
import com.thedndpub.data.repositories.language.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<LanguageRecord> getAll() {
        return languageRepository.findAll();
    }

    public Optional<LanguageRecord> getById(String id) {
        return languageRepository.findAll().stream()
                .filter(language -> language.id().equals(id))
                .findFirst();
    }

    public List<LanguageRecord> getByName(String name) {
        return languageRepository.findAll().stream()
                .filter(language -> language.name().equals(name))
                .toList();
    }
}
