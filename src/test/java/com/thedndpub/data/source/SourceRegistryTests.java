package com.thedndpub.data.source;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.source.PublicationMetadataStatus;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.source.SourceCodeDte;
import com.thedndpub.data.dte.source.SourceRegistryFileDte;
import com.thedndpub.data.services.PublicationCatalog;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SourceRegistryTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private SourceRegistry registry;

    @Autowired
    private PublicationCatalog publicationCatalog;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registersEveryDiscoveredSourceCodeAndPublicationStatus() {
        assertEquals(181, registry.size());
        assertEquals(66, registry.publicationCount());
        assertEquals(115, registry.size() - registry.publicationCount());
        assertNotNull(registry.find("HFDoMM"));
        assertEquals(null, registry.find("HFDoMM").publication());
        assertEquals(PublicationMetadataStatus.AVAILABLE, registry.find("PHB").publicationMetadataStatus());
        assertEquals(PublicationMetadataStatus.UNAVAILABLE,
                registry.find("DSotDQ").publicationMetadataStatus());
        assertEquals(PublicationMetadataStatus.UNAVAILABLE, registry.find("PiP").publicationMetadataStatus());
        for (var source : registry.records()) {
            assertNotNull(registry.find(source.identity().code()));
        }
        assertNull(registry.find("definitely-unknown-source"));
    }

    @Test
    void resolvesKnownPublicationAliasesWithoutChangingSourceIdentity() {
        var aliases = java.util.Map.of(
                "PS-Z", "PSZ",
                "PS-I", "PSI",
                "PS-K", "PSK",
                "PS-A", "PSA",
                "PS-X", "PSX",
                "PS-D", "PSD");
        for (var aliasEntry : aliases.entrySet()) {
            String alias = aliasEntry.getKey();
            var resolved = registry.find(alias);
            assertNotNull(resolved);
            assertNotNull(resolved.publication());
            assertEquals(aliasEntry.getValue(), resolved.identity().code());
            assertEquals(
                    aliasEntry.getValue(),
                    publicationCatalog.findBySourceCode(alias).id().value());
        }
        assertEquals("PHB", registry.find("phb").identity().code());
    }

    @Test
    void rejectsAliasConflictsWithSourceCodes() {
        SourceRegistryFileDte source = new SourceRegistryFileDte(List.of(
                new SourceCodeDte("ONE", List.of("TWO")),
                new SourceCodeDte("TWO", List.of())));

        assertThrows(IllegalStateException.class, () -> new SourceRegistry(source, publicationCatalog));

        SourceRegistryFileDte duplicateAlias = new SourceRegistryFileDte(List.of(
                new SourceCodeDte("ONE", List.of("SHARED")),
                new SourceCodeDte("TWO", List.of("shared"))));
        assertThrows(IllegalStateException.class, () -> new SourceRegistry(duplicateAlias, publicationCatalog));
    }

    @Test
    void sourceReferenceSerializationKeepsCodeAndOmitsMissingPage() throws Exception {
        JsonNode json = objectMapper.valueToTree(new SourceReference(new SourceIdentity("PHB"), null));

        assertEquals("PHB", json.get("source").asText());
        assertNull(json.get("page"));

        JsonNode locatedReference = objectMapper.valueToTree(new SourceReference("PHB", 42));
        assertEquals(42, locatedReference.get("page").asInt());
    }

    @Test
    void loadsRegistryFromJarClasspathResource() throws Exception {
        Path jarPath = temporaryDirectory.resolve("source-registry.jar");
        try (InputStream input = new ClassPathResource("data/sources/registry.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/sources/registry.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            ClassPathResource resource = new ClassPathResource("data/sources/registry.json", classLoader);
            try (InputStream input = resource.getInputStream()) {
                JsonNode json = objectMapper.readTree(input);
                assertEquals(181, json.get("sources").size());
            }
        }
    }
}
