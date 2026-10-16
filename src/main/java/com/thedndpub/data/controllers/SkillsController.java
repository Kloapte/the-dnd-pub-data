package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.skill.SkillDto;
import com.thedndpub.data.dto.skill.SkillSummaryDto;
import com.thedndpub.data.services.SkillsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public final class SkillsController {
    private final SkillsService skillsService;

    public SkillsController(SkillsService skillsService) {
        this.skillsService = skillsService;
    }

    @GetMapping
    public List<SkillSummaryDto> findSkills(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return skillsService.findSkills(name, source);
    }

    @GetMapping("/{id}")
    public SkillDto findById(@PathVariable String id) {
        return skillsService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<SkillDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return skillsService.findByName(name, source);
    }
}
