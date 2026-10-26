package com.thedndpub.data.repositories.corerules;

import com.thedndpub.data.repositories.CoreRulesRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RuleRepository extends CoreRulesDomainRepository {
    public RuleRepository(CoreRulesRepository catalogRepository, ObjectMapper objectMapper) {
        super(catalogRepository, objectMapper, "rule");
    }
}
