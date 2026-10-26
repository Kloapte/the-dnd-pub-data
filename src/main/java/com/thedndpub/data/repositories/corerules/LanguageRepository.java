package com.thedndpub.data.repositories.corerules;

import com.thedndpub.data.dto.corerules.language.LanguageRecord;
import com.thedndpub.data.repositories.CoreRulesRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LanguageRepository extends CoreRulesDomainRepository {
    public LanguageRepository(CoreRulesRepository catalogRepository, ObjectMapper objectMapper) {
        super(catalogRepository, objectMapper, "language");
    }

    public List<LanguageRecord> findAllLanguages() {
        return readAllModels(LanguageRecord[].class);
    }

    public Optional<LanguageRecord> findLanguage(String source, String slug) {
        return readModel(source, slug, LanguageRecord.class);
    }
}
