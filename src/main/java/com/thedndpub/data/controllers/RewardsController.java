package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.reward.RewardDetailDto;
import com.thedndpub.data.dto.reward.RewardSummaryDto;
import com.thedndpub.data.services.RewardsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public final class RewardsController {
    private final RewardsService service;

    public RewardsController(RewardsService service) {
        this.service = service;
    }

    @GetMapping
    public List<RewardSummaryDto> findAll(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type) {
        return service.findAll(source, type);
    }

    @GetMapping("/by-name/{name}")
    public List<RewardSummaryDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return service.findByName(name, source);
    }

    @GetMapping("/{id}")
    public RewardDetailDto findById(@PathVariable String id) {
        return service.findById(id);
    }
}
