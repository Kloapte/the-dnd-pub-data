package com.thedndpub.data;

import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class CoreRulesCanonicalAssertions {
    private CoreRulesCanonicalAssertions() {
    }

    static <T> void assertAllRecordsRoundTrip(
            ObjectMapper objectMapper,
            Resource canonicalDataset,
            Class<T[]> recordArrayType,
            int expectedRecordCount) throws Exception {
        byte[] canonicalBytes;
        try (InputStream input = canonicalDataset.getInputStream()) {
            canonicalBytes = input.readAllBytes();
        }

        JsonNode canonical = objectMapper.readTree(canonicalBytes);
        T[] records = objectMapper.readValue(canonicalBytes, recordArrayType);

        assertEquals(expectedRecordCount, records.length);
        assertJsonEquivalent(
                canonical,
                objectMapper.readTree(objectMapper.writeValueAsBytes(records)),
                "Canonical DTO round-trip changed JSON meaning");
    }

    static void assertAllApiLookupsMatchCanonicalDataset(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            Resource canonicalDataset,
            String collectionPath,
            String idPathTemplate,
            String namePathTemplate,
            int expectedRecordCount) throws Exception {
        JsonNode canonical;
        try (InputStream input = canonicalDataset.getInputStream()) {
            canonical = objectMapper.readTree(input);
        }

        assertEquals(expectedRecordCount, canonical.size());
        assertJsonEquivalent(
                canonical,
                getJson(mockMvc, objectMapper, collectionPath),
                "Collection endpoint changed canonical JSON: " + collectionPath);

        Map<String, ArrayNode> canonicalByName = new LinkedHashMap<>();
        for (JsonNode record : canonical) {
            String id = record.path("id").asString();
            String name = record.path("name").asString();
            assertNotNull(id, "Canonical record has no id: " + record);
            assertNotNull(name, "Canonical record has no name: " + record);
            assertTrue(!id.isBlank(), "Canonical record has a blank id: " + record);
            assertTrue(!name.isBlank(), "Canonical record has a blank name: " + record);

            JsonNode byId = getJson(mockMvc, objectMapper, idPathTemplate, id);
            assertJsonEquivalent(record, byId, "ID endpoint changed canonical record " + id);

            canonicalByName.computeIfAbsent(name, ignored -> objectMapper.createArrayNode()).add(record);
        }

        for (Map.Entry<String, ArrayNode> entry : canonicalByName.entrySet()) {
            JsonNode byName = getJson(mockMvc, objectMapper, namePathTemplate, entry.getKey());
            assertJsonEquivalent(
                    entry.getValue(), byName, "Name endpoint changed canonical records named " + entry.getKey());
        }
    }

    private static void assertJsonEquivalent(JsonNode expected, JsonNode actual, String message) {
        String difference = firstDifference("$", expected, actual);
        assertEquals(null, difference, () -> message + ": " + difference);
    }

    static String firstDifference(String path, JsonNode expected, JsonNode actual) {
        if (expected.equals(actual)
                || expected.isNumber() && actual.isNumber()
                && expected.decimalValue().compareTo(actual.decimalValue()) == 0) {
            return null;
        }
        if (expected.isArray() && actual.isArray()) {
            if (expected.size() != actual.size()) {
                return path + " array length expected " + expected.size() + " but was " + actual.size();
            }
            for (int index = 0; index < expected.size(); index++) {
                String difference = firstDifference(path + "[" + index + "]", expected.get(index), actual.get(index));
                if (difference != null) {
                    return difference;
                }
            }
            return null;
        }
        if (expected.isObject() && actual.isObject()) {
            for (String name : expected.propertyNames()) {
                if (!actual.has(name)) {
                    return path + "." + name + " missing after round-trip";
                }
                String difference = firstDifference(path + "." + name, expected.get(name), actual.get(name));
                if (difference != null) {
                    return difference;
                }
            }
            for (String name : actual.propertyNames()) {
                if (!expected.has(name)) {
                    return path + "." + name + " was added by round-trip";
                }
            }
            return null;
        }
        if (expected.isContainer() || actual.isContainer()) {
            if (!expected.isContainer() || !actual.isContainer()) {
                return path + " expected " + expected + " but was " + actual;
            }
            return path + " has incompatible container node types";
        }
        return path + " expected " + expected + " but was " + actual
                + " (expected node=" + expected.getClass().getSimpleName()
                + ", container=" + expected.isContainer() + ", array=" + expected.isArray()
                + "; actual node=" + actual.getClass().getSimpleName()
                + ", container=" + actual.isContainer() + ", array=" + actual.isArray() + ")";
    }

    private static JsonNode getJson(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String pathTemplate,
            String... pathVariables) throws Exception {
        MvcResult result = mockMvc.perform(get(pathTemplate, (Object[]) pathVariables))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsByteArray());
    }
}
