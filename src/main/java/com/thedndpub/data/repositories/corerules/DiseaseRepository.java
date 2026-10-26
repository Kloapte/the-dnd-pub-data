package com.thedndpub.data.repositories.corerules;

import com.thedndpub.data.repositories.CoreRulesRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DiseaseRepository extends CoreRulesDomainRepository {
    public DiseaseRepository(CoreRulesRepository catalogRepository, ObjectMapper objectMapper) {
        super(catalogRepository, objectMapper, "disease");
    }
}
