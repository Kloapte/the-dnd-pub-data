package com.thedndpub.data.repositories.corerules;

import com.thedndpub.data.dto.corerules.status.StatusRecord;
import com.thedndpub.data.repositories.CoreRulesRepository;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class StatusRepository extends CoreRulesDomainRepository {
    public StatusRepository(CoreRulesRepository catalogRepository, ObjectMapper objectMapper) {
        super(catalogRepository, objectMapper, "status");
    }

    public List<StatusRecord> findAllStatuses() {
        return readAllModels(StatusRecord[].class);
    }

    public Optional<StatusRecord> findStatus(String source, String slug) {
        return readModel(source, slug, StatusRecord.class);
    }
}
