package com.thedndpub.data.services.corerules;

import com.thedndpub.data.dto.corerules.language.LanguageRecord;
import com.thedndpub.data.repositories.corerules.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService extends CoreRulesDomainService {
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository repository) {
        super(repository);
        this.languageRepository = repository;
    }

    public List<LanguageRecord> findAllLanguages() {
        return languageRepository.findAllLanguages();
    }

    public Optional<LanguageRecord> findLanguage(String source, String slug) {
        return languageRepository.findLanguage(source, slug);
    }
}
