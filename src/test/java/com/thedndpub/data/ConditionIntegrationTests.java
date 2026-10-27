package com.thedndpub.data;

import com.thedndpub.data.dto.condition.ConditionRecord;
import com.thedndpub.data.dto.condition.effects.AttackRollModifierEffect;
import com.thedndpub.data.dto.condition.effects.ConditionEffect;
import com.thedndpub.data.dto.condition.effects.FatalExhaustionThresholdEffect;
import com.thedndpub.data.repositories.condition.ConditionRepository;
import com.thedndpub.data.services.condition.ConditionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ConditionIntegrationTests {
    @Autowired
    private ConditionRepository conditionRepository;

    @Autowired
    private ConditionService conditionService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void repositoryLoadsEveryCondition() {
        assertEquals(30, conditionRepository.findAll().size());
        assertEquals(135, conditionRepository.findAll().stream()
                .mapToInt(condition -> condition.effects().size())
                .sum());
    }

    @Test
    void serviceFindsByIdAndReturnsAllExactNameMatches() {
        ConditionRecord condition = conditionService.getById("condition:xphb:blinded").orElseThrow();
        assertEquals("Blinded", condition.name());
        assertTrue(conditionService.getById("unknown").isEmpty());
        assertEquals(2, conditionService.getByName("Blinded").size());
        assertTrue(conditionService.getByName("unknown").isEmpty());
    }

    @Test
    void mapsPolymorphicAndEditionSpecificEffects() {
        ConditionRecord blinded = conditionService.getById("condition:phb:blinded").orElseThrow();
        ConditionEffect attackModifier = blinded.effects().stream()
                .filter(effect -> effect instanceof AttackRollModifierEffect)
                .findFirst()
                .orElseThrow();
        assertInstanceOf(AttackRollModifierEffect.class, attackModifier);

        ConditionRecord exhaustion = conditionService.getById("condition:xphb:exhaustion").orElseThrow();
        assertTrue(exhaustion.effects().stream().anyMatch(FatalExhaustionThresholdEffect.class::isInstance));
        assertEquals("ADD_ONE_LEVEL", exhaustion.levelMechanics().application().name());
    }

    @Test
    void exposesThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/conditions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(30));

        mockMvc.perform(get("/api/conditions/{id}", "condition:xphb:blinded"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("condition:xphb:blinded"));

        mockMvc.perform(get("/api/conditions/name/{name}", "Blinded"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/conditions/{id}", "unknown"))
                .andExpect(status().isNotFound());
    }
}
