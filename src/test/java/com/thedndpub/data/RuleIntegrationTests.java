package com.thedndpub.data;

import com.thedndpub.data.dto.rule.RuleRecord;
import com.thedndpub.data.repositories.rule.RuleRepository;
import com.thedndpub.data.services.rule.RuleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RuleIntegrationTests {
    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:normalized/core-rules/rules.json")
    private Resource rulesDataset;

    @Test
    void repositoryLoadsEveryRuleWithUniqueIdentifiers() {
        assertEquals(231, ruleRepository.findAll().size());
        assertEquals(231, ruleRepository.findAll().stream()
                .map(RuleRecord::id)
                .distinct()
                .count());
    }

    @Test
    void serviceFindsByIdAndReturnsAllExactNameMatches() {
        RuleRecord rule = ruleService.getById("rule:aag:crashing").orElseThrow();
        assertEquals("Crashing", rule.name());
        assertTrue(ruleService.getById("rule:unknown:missing").isEmpty());

        assertEquals(2, ruleService.getByName("Healing").size());
        assertTrue(ruleService.getByName("unknown").isEmpty());
    }

    @Test
    void exposesThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(231));

        mockMvc.perform(get("/api/rules/{id}", "rule:aag:crashing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("rule:aag:crashing"));

        mockMvc.perform(get("/api/rules/name/{name}", "Healing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/rules/{id}", "rule:unknown:missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void allEndpointPreservesCanonicalRuleJson() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        CoreRulesCanonicalAssertions.assertAllApiLookupsMatchCanonicalDataset(
                mockMvc, objectMapper, rulesDataset,
                "/api/rules", "/api/rules/{id}", "/api/rules/name/{name}", 231);
    }
}
