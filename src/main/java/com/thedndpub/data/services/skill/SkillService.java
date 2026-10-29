package com.thedndpub.data.services.skill;

import com.thedndpub.data.dto.skill.SkillRecord;
import com.thedndpub.data.repositories.skill.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<SkillRecord> getAll() {
        return skillRepository.findAll();
    }

    public Optional<SkillRecord> getById(String id) {
        return skillRepository.findAll().stream()
                .filter(skill -> skill.id().equals(id))
                .findFirst();
    }

    public List<SkillRecord> getByName(String name) {
        return skillRepository.findAll().stream()
                .filter(skill -> skill.name().equals(name))
                .toList();
    }
}
