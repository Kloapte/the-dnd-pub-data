package com.thedndpub.data.repositories.corerules;

import com.thedndpub.data.dto.corerules.sense.SenseRecord;
import com.thedndpub.data.repositories.CoreRulesRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SenseRepository extends CoreRulesDomainRepository {
    public SenseRepository(CoreRulesRepository catalogRepository, ObjectMapper objectMapper) {
        super(catalogRepository, objectMapper, "sense");
    }

    public List<SenseRecord> findAllSenses() {
        return readAllModels(SenseRecord[].class);
    }

    public Optional<SenseRecord> findSense(String source, String slug) {
        return readModel(source, slug, SenseRecord.class);
    }
}
