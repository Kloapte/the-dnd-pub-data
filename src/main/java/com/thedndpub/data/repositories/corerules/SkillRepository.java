package com.thedndpub.data.repositories.corerules;

import com.thedndpub.data.repositories.CoreRulesRepository;
import com.thedndpub.data.dto.corerules.skill.SkillRecord;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class SkillRepository extends CoreRulesDomainRepository {
    public SkillRepository(CoreRulesRepository catalogRepository, ObjectMapper objectMapper) {
        super(catalogRepository, objectMapper, "skill");
    }

    public List<SkillRecord> findAllSkills() {
        return readAllModels(SkillRecord[].class);
    }

    public Optional<SkillRecord> findSkill(String source, String slug) {
        return readModel(source, slug, SkillRecord.class);
    }
}
