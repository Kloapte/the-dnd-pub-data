package com.thedndpub.data.controllers.skill;

import com.thedndpub.data.dto.skill.SkillRecord;
import com.thedndpub.data.services.skill.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Canonical Skills and their associated ability checks")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @Operation(summary = "Get all skills")
    @ApiResponse(responseCode = "200", description = "Skills in normalized dataset order")
    public List<SkillRecord> getAll() {
        return skillService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a skill by its exact identifier")
    @ApiResponse(responseCode = "404", description = "No skill has this identifier")
    public SkillRecord getById(@Parameter(description = "Exact skill identifier") @PathVariable String id) {
        return skillService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get skills by exact name")
    @ApiResponse(responseCode = "200", description = "All skills with this exact name")
    public List<SkillRecord> getByName(@Parameter(description = "Exact skill name") @PathVariable String name) {
        return skillService.getByName(name);
    }
}
