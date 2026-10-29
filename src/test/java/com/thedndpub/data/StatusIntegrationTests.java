package com.thedndpub.data;

import com.thedndpub.data.dto.status.StatusRecord;
import com.thedndpub.data.dto.status.mechanics.EndingConditionMechanic;
import com.thedndpub.data.dto.status.mechanics.HitPointThresholdMechanic;
import com.thedndpub.data.dto.status.mechanics.MaintenanceDc;
import com.thedndpub.data.dto.status.mechanics.MaintenanceSaveMechanic;
import com.thedndpub.data.dto.status.mechanics.RollModifierMechanic;
import com.thedndpub.data.dto.status.mechanics.StatusMechanic;
import com.thedndpub.data.dto.status.mechanics.TurnRestrictionMechanic;
import com.thedndpub.data.repositories.status.StatusRepository;
import com.thedndpub.data.services.status.StatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StatusIntegrationTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private StatusService statusService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("classpath:normalized/core-rules/statuses.json")
    private Resource statusesDataset;

    @Test
    void repositoryLoadsEveryStatusWithUniqueIds() {
        assertEquals(5, statusRepository.findAll().size());
        assertEquals(5, statusRepository.findAll().stream().map(StatusRecord::id).distinct().count());
    }

    @Test
    void deserializesAndSerializesEveryStatusWithoutChangingJsonMeaning() throws Exception {
        byte[] sourceBytes;
        try (InputStream input = statusesDataset.getInputStream()) {
            sourceBytes = input.readAllBytes();
        }
        JsonNode source = objectMapper.readTree(sourceBytes);
        StatusRecord[] statuses = objectMapper.readValue(sourceBytes, StatusRecord[].class);

        assertEquals(5, statuses.length);
        assertEquals(source, objectMapper.readTree(objectMapper.writeValueAsString(statuses)));
    }

    @Test
    void deserializesAllMechanicVariantsAndBothMaintenanceDcForms() {
        Set<Class<?>> mechanicTypes = statusRepository.findAll().stream()
                .flatMap(status -> status.mechanics().stream())
                .map(Object::getClass)
                .collect(Collectors.toSet());
        assertEquals(Set.of(
                HitPointThresholdMechanic.class,
                TurnRestrictionMechanic.class,
                RollModifierMechanic.class,
                MaintenanceSaveMechanic.class,
                EndingConditionMechanic.class), mechanicTypes);

        Set<?> formulas = statusRepository.findAll().stream()
                .flatMap(status -> status.mechanics().stream())
                .filter(MaintenanceSaveMechanic.class::isInstance)
                .map(MaintenanceSaveMechanic.class::cast)
                .map(MaintenanceSaveMechanic::dc)
                .map(MaintenanceDc::formula)
                .collect(Collectors.toSet());
        assertEquals(2, formulas.size());
    }

    @Test
    void rejectsInvalidMechanicsAndConditionalMaintenanceSaveValues() {
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"HIT_POINT_THRESHOLD\",\"fraction\":1.1,\"comparison\":\"below\"}",
                StatusMechanic.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"TURN_RESTRICTION\",\"restriction\":\"cannot-cast-spell\","
                        + "\"appliesWhen\":\"first-turn-of-combat\"}", StatusMechanic.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"ROLL_MODIFIER\",\"roll\":\"damage\",\"direction\":\"advantage\"}",
                StatusMechanic.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"MAINTENANCE_SAVE\",\"ability\":\"constitution\",\"trigger\":\"damage\","
                        + "\"dc\":{\"formula\":\"higher-of-base-or-damage-fraction\",\"base\":10}}",
                StatusMechanic.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"MAINTENANCE_SAVE\",\"ability\":\"constitution\","
                        + "trigger\":\"damage\",\"dc\":{\"formula\":\"fixed\",\"base\":10},"
                        + "\"requirement\":\"dm-may-require\"}", StatusMechanic.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"ENDING_CONDITION\",\"when\":\"voluntarily\","
                        + "\"actionRequired\":true}", StatusMechanic.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"ENDING_CONDITION\",\"when\":\"dies\",\"automatic\":false}",
                StatusMechanic.class));
    }

    @Test
    void findsStatusesByIdAndExactName() {
        StatusRecord concentration = statusService.getById("status:phb:concentration").orElseThrow();
        assertEquals("Concentration", concentration.name());
        assertTrue(statusService.getById("status:missing:missing").isEmpty());
        assertEquals(2, statusService.getByName("Concentration").size());
        assertTrue(statusService.getByName("Unknown Status").isEmpty());
    }

    @Test
    void exposesAllThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));

        mockMvc.perform(get("/api/statuses/{id}", "status:phb:concentration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("status:phb:concentration"));

        mockMvc.perform(get("/api/statuses/name/{name}", "Concentration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/statuses/{id}", "status:missing:missing"))
                .andExpect(status().isNotFound());
    }
}
