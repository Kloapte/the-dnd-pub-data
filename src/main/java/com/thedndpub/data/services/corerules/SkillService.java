package com.thedndpub.data.services.corerules;

import com.thedndpub.data.repositories.corerules.SkillRepository;
import com.thedndpub.data.dto.corerules.skill.SkillRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService extends CoreRulesDomainService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository repository) {
        super(repository);
        this.skillRepository = repository;
    }

    public List<SkillRecord> findAllSkills() {
        return skillRepository.findAllSkills();
    }

    public Optional<SkillRecord> findSkill(String source, String slug) {
        return skillRepository.findSkill(source, slug);
    }
}
