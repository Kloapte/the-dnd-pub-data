package com.thedndpub.data;

import com.thedndpub.data.dto.action.ActionAttackResolution;
import com.thedndpub.data.dto.action.ActionRecord;
import com.thedndpub.data.repositories.action.ActionRepository;
import com.thedndpub.data.services.action.ActionService;
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
class ActionIntegrationTests {
    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ActionService actionService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void repositoryLoadsEveryAction() {
        assertEquals(46, actionRepository.findAll().size());
        assertEquals(63, actionRepository.findAll().stream()
                .mapToInt(action -> action.operations().size())
                .sum());
    }

    @Test
    void serviceFindsByIdAndReturnsAllExactNameMatches() {
        ActionRecord action = actionService.getById("action:dmg:activate-an-item").orElseThrow();
        assertEquals("Activate an Item", action.name());
        assertTrue(actionService.getById("unknown").isEmpty());

        assertEquals(2, actionService.getByName("Attack").size());
        assertTrue(actionService.getByName("unknown").isEmpty());
    }

    @Test
    void mapsPolymorphicOperations() {
        ActionRecord attack = actionService.getById("action:phb:attack").orElseThrow();
        assertInstanceOf(ActionAttackResolution.class, attack.operations().getFirst());
    }

    @Test
    void exposesThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/actions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(46));

        mockMvc.perform(get("/api/actions/{id}", "action:dmg:activate-an-item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("action:dmg:activate-an-item"));

        mockMvc.perform(get("/api/actions/name/{name}", "Attack"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/actions/{id}", "unknown"))
                .andExpect(status().isNotFound());
    }
}
