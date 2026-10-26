package com.thedndpub.data.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CoreRulesRepositoryStartupTests {
    private static final String ACTION_RESOURCE = "normalized/core-rules/actions.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void rejectsDuplicateCanonicalIdentitiesAtStartup() throws Exception {
        ArrayNode duplicateRecords = objectMapper.createArrayNode();
        JsonNode firstAction = readClasspathJson(ACTION_RESOURCE).get(0);
        duplicateRecords.add(firstAction);
        duplicateRecords.add(firstAction);

        assertThrows(IllegalStateException.class,
                () -> createRepository(objectMapper.writeValueAsBytes(duplicateRecords)));
    }

    @Test
    void rejectsIdentityAndSourceMetadataMismatchAtStartup() throws Exception {
        ArrayNode records = objectMapper.createArrayNode();
        ObjectNode record = (ObjectNode) readClasspathJson(ACTION_RESOURCE).get(0).deepCopy();
        record.put("name", "A Different Action");
        records.add(record);

        assertThrows(IllegalStateException.class,
                () -> createRepository(objectMapper.writeValueAsBytes(records)));
    }

    @Test
    void rejectsReferencesAbsentFromPinnedSourceIdentitiesAtStartup() throws Exception {
        ArrayNode records = objectMapper.createArrayNode();
        ObjectNode record = (ObjectNode) readClasspathJson(ACTION_RESOURCE).get(0).deepCopy();
        ArrayNode references = record.withArray("references");
        ObjectNode invalidReference = objectMapper.createObjectNode();
        invalidReference.put("targetType", "spell");
        invalidReference.put("id", "spell:phb:not-in-the-pinned-catalog");
        references.add(invalidReference);
        records.add(record);

        assertThrows(IllegalStateException.class,
                () -> createRepository(objectMapper.writeValueAsBytes(records)));
    }

    private CoreRulesRepository createRepository(byte[] invalidActions) {
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getResource(anyString())).thenAnswer(invocation -> {
            String location = invocation.getArgument(0);
            if (location.equals("classpath:" + ACTION_RESOURCE)) {
                return new ByteArrayResource(invalidActions);
            }
            return new ClassPathResource(location.substring("classpath:".length()));
        });
        return new CoreRulesRepository(objectMapper, context);
    }

    private JsonNode readClasspathJson(String resourcePath) throws Exception {
        Resource resource = new ClassPathResource(resourcePath);
        try (InputStream input = resource.getInputStream()) {
            return objectMapper.readTree(input);
        }
    }
}
