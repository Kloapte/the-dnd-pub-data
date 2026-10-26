package com.thedndpub.data.controllers.corerules.skill;

import com.thedndpub.data.dto.corerules.skill.SkillRecord;
import com.thedndpub.data.services.corerules.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/core-rules/skill")
@Tag(name = "Skill", description = "Read-only canonical Skill catalog")
public class SkillController {
    private final SkillService service;

    public SkillController(SkillService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List canonical Skills")
    @ApiResponse(responseCode = "200", description = "Typed Skill records in identity order")
    public List<SkillRecord> findAll() {
        return service.findAllSkills();
    }

    @GetMapping("/{source}/{slug}")
    @Operation(summary = "Find a Skill by source-qualified identity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canonical Skill record"),
            @ApiResponse(responseCode = "404", description = "Skill not found")
    })
    public SkillRecord findByIdentity(
            @Parameter(description = "Lowercase canonical source component") @PathVariable("source") String source,
            @Parameter(description = "Canonical Skill name slug") @PathVariable("slug") String slug) {
        return service.findSkill(source, slug).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Canonical Skill identity not found: skill:" + source + ":" + slug));
    }
}
