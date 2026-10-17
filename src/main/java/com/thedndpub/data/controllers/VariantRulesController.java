package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.variantrule.VariantRuleDetailDto;
import com.thedndpub.data.dto.variantrule.VariantRuleSummaryDto;
import com.thedndpub.data.services.VariantRulesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/variant-rules")
public final class VariantRulesController {
    private final VariantRulesService service;

    public VariantRulesController(VariantRulesService service) {
        this.service = service;
    }

    @GetMapping
    public List<VariantRuleSummaryDto> find(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String ruleType) {
        return service.find(name, source, ruleType);
    }

    @GetMapping("/{id}")
    public VariantRuleDetailDto byId(@PathVariable String id) {
        return service.byId(id);
    }

    @GetMapping("/by-name/{name}")
    public List<VariantRuleDetailDto> byName(
            @PathVariable String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        String prefix = "/api/variant-rules/by-name/";
        String uri = request.getRequestURI();
        String requestedName = uri.startsWith(prefix)
                ? UriUtils.decode(uri.substring(prefix.length()), StandardCharsets.UTF_8)
                : name;
        return service.byName(requestedName, source);
    }
}
