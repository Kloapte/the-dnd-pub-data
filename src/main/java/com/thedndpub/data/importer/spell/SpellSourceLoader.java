package com.thedndpub.data.importer.spell;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.spell.SpellFileDte;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Loads all spell source files from a classpath directory, including resources inside packaged JARs. */
public final class SpellSourceLoader {
    private static final String RESOURCE_PATTERN = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            + "data/spells/*.json";

    private final ObjectMapper objectMapper;
    private final ResourcePatternResolver resourceResolver;

    public SpellSourceLoader(ObjectMapper objectMapper) {
        this(objectMapper, new PathMatchingResourcePatternResolver());
    }

    public SpellSourceLoader(ObjectMapper objectMapper, ResourcePatternResolver resourceResolver) {
        this.objectMapper = objectMapper;
        this.resourceResolver = resourceResolver;
    }

    public SpellSourceCorpus load() {
        Resource[] resources;
        try {
            resources = resourceResolver.getResources(RESOURCE_PATTERN);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not enumerate spell source resources", exception);
        }
        if (resources.length == 0) {
            throw new IllegalStateException("No spell source resources found at " + RESOURCE_PATTERN);
        }

        Arrays.sort(resources, Comparator.comparing(SpellSourceLoader::resourceName));
        Set<String> resourceNames = new HashSet<>();
        List<SpellSourceDocument> documents = new ArrayList<>(resources.length);
        for (Resource resource : resources) {
            String resourceName = resourceName(resource);
            if (!resourceNames.add(resourceName)) {
                throw new IllegalStateException("Duplicate spell source resource name: " + resourceName);
            }
            try (var input = resource.getInputStream()) {
                SpellFileDte data = objectMapper.readerFor(SpellFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(input);
                if (data.spells() == null) {
                    throw new IllegalStateException("Missing spell collection in " + resourceName);
                }
                documents.add(new SpellSourceDocument(resourceName, data));
            } catch (IOException exception) {
                throw new IllegalStateException("Could not deserialize spell source " + resourceName, exception);
            }
        }
        return new SpellSourceCorpus(documents);
    }

    private static String resourceName(Resource resource) {
        return resource.getFilename() == null ? resource.getDescription() : resource.getFilename();
    }
}
