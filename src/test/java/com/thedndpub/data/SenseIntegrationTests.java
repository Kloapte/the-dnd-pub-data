package com.thedndpub.data;

import com.thedndpub.data.dto.sense.SenseRecord;
import com.thedndpub.data.dto.sense.capabilities.ConditionInteractionCapability;
import com.thedndpub.data.dto.sense.capabilities.DetectedTargetsPrecision;
import com.thedndpub.data.dto.sense.capabilities.NonvisualPerceptionCapability;
import com.thedndpub.data.dto.sense.capabilities.SightCapability;
import com.thedndpub.data.dto.sense.capabilities.VibrationOriginPrecision;
import com.thedndpub.data.repositories.sense.SenseRepository;
import com.thedndpub.data.services.sense.SenseService;
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
class SenseIntegrationTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SenseRepository senseRepository;

    @Autowired
    private SenseService senseService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("classpath:normalized/core-rules/senses.json")
    private Resource sensesDataset;

    @Test
    void repositoryLoadsEverySenseWithUniqueIds() {
        assertEquals(8, senseRepository.findAll().size());
        assertEquals(8, senseRepository.findAll().stream().map(SenseRecord::id).distinct().count());
    }

    @Test
    void deserializesAndSerializesAllSensesWithoutChangingJsonMeaning() throws Exception {
        byte[] sourceBytes;
        try (InputStream input = sensesDataset.getInputStream()) {
            sourceBytes = input.readAllBytes();
        }
        JsonNode source = objectMapper.readTree(sourceBytes);
        SenseRecord[] senses = objectMapper.readValue(sourceBytes, SenseRecord[].class);
        assertEquals(8, senses.length);

        JsonNode serialized = objectMapper.readTree(objectMapper.writeValueAsString(senses));
        assertEquals(source, serialized);
    }

    @Test
    void deserializesAllCapabilityAndLocationPrecisionVariantsAsConcreteTypes() {
        Set<Class<?>> capabilityTypes = senseRepository.findAll().stream()
                .flatMap(sense -> sense.capabilities().stream())
                .map(Object::getClass)
                .collect(Collectors.toSet());
        assertTrue(capabilityTypes.containsAll(Set.of(
                SightCapability.class, NonvisualPerceptionCapability.class, ConditionInteractionCapability.class)));

        assertTrue(senseRepository.findAll().stream()
                .flatMap(sense -> sense.capabilities().stream())
                .filter(NonvisualPerceptionCapability.class::isInstance)
                .map(NonvisualPerceptionCapability.class::cast)
                .flatMap(capability -> capability.locationPrecision() == null
                        ? java.util.stream.Stream.empty()
                        : java.util.stream.Stream.of(capability.locationPrecision()))
                .anyMatch(VibrationOriginPrecision.class::isInstance));
        assertTrue(senseRepository.findAll().stream()
                .flatMap(sense -> sense.capabilities().stream())
                .filter(NonvisualPerceptionCapability.class::isInstance)
                .map(NonvisualPerceptionCapability.class::cast)
                .anyMatch(capability -> capability.locationPrecision() instanceof DetectedTargetsPrecision));
    }

    @Test
    void rejectsInvalidCapabilityAndNestedUnionPayloads() {
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"SIGHT\"}", com.thedndpub.data.dto.sense.capabilities.SenseCapability.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"SIGHT\",\"perceives\":[\"x-ray\"]}",
                com.thedndpub.data.dto.sense.capabilities.SenseCapability.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"NONVISUAL_PERCEPTION\",\"medium\":\"vibration\","
                        + "\"locationPrecision\":{\"kind\":\"DETECTED_TARGETS\"}}",
                com.thedndpub.data.dto.sense.capabilities.SenseCapability.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"DETECTED_TARGETS\",\"targets\":[]}",
                com.thedndpub.data.dto.sense.capabilities.SenseLocationPrecision.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"UNKNOWN\"}", com.thedndpub.data.dto.sense.capabilities.SenseCapability.class));
    }

    @Test
    void findsSensesByIdAndExactName() {
        SenseRecord darkvision = senseService.getById("sense:phb:darkvision").orElseThrow();
        assertEquals("Darkvision", darkvision.name());
        assertTrue(senseService.getById("sense:missing:missing").isEmpty());
        assertEquals(2, senseService.getByName("Darkvision").size());
        assertTrue(senseService.getByName("Unknown Sense").isEmpty());
    }

    @Test
    void exposesAllThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/senses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8));

        mockMvc.perform(get("/api/senses/{id}", "sense:phb:darkvision"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sense:phb:darkvision"));

        mockMvc.perform(get("/api/senses/name/{name}", "Darkvision"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/senses/{id}", "sense:missing:missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void everyApiLookupMatchesCanonicalSenses() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        CoreRulesCanonicalAssertions.assertAllApiLookupsMatchCanonicalDataset(
                mockMvc, objectMapper, sensesDataset,
                "/api/senses", "/api/senses/{id}", "/api/senses/name/{name}", 8);
    }
}
