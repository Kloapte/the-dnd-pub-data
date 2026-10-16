package com.thedndpub.data.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.BooksController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.dto.book.PublicationDto;
import com.thedndpub.data.dto.book.PublicationMapper;
import com.thedndpub.data.dte.book.BookDte;
import com.thedndpub.data.dte.book.BooksFileDte;
import com.thedndpub.data.dte.book.StructuredBookHeaderDte;
import com.thedndpub.data.dte.book.TextBookHeaderDte;
import com.thedndpub.data.services.PublicationCatalog;
import com.thedndpub.data.services.PublicationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PublicationCatalogTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PublicationCatalog catalog;

    @Autowired
    private PublicationsService service;

    private MockMvc mockMvc;

    @BeforeEach
    void configureController() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BooksController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
    }

    @Test
    void deserializesAndMapsCompletePublicationCorpus() throws Exception {
        try (InputStream input = new ClassPathResource("data/books.json").getInputStream()) {
            BooksFileDte source = strictReader().readValue(input);
            assertEquals(66, source.book().size());
            assertEquals(66, catalog.size());
            assertEquals(66, source.book().stream().map(BookDte::source).distinct().count());
            assertEquals(66, source.book().stream().map(BookDte::id).distinct().count());
        }
    }

    @Test
    void loadsBookDataFromJarClasspath() throws Exception {
        Path jarPath = temporaryDirectory.resolve("publication-data.jar");
        try (InputStream input = new ClassPathResource("data/books.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/books.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            ClassPathResource resource = new ClassPathResource("data/books.json", classLoader);
            try (InputStream input = resource.getInputStream()) {
                assertEquals(66, strictReader().<BooksFileDte>readValue(input).book().size());
            }
        }
    }

    @Test
    void catalogSortsDeterministicallyAndKeepsSourceAliases() {
        List<String> names = catalog.summaries().stream().map(summary -> summary.name().toLowerCase()).toList();
        List<String> sortedNames = names.stream().sorted().toList();

        assertEquals(sortedNames, names);
        assertEquals("PSZ", catalog.findBySourceCode("PS-Z").id().value());
        assertEquals("PSZ", catalog.findBySourceCode("PSZ").id().value());
        assertNull(catalog.findBySourceCode("DSotDQ"));
    }

    @Test
    void titleLookupReturnsAllAmbiguousAliasMatches() {
        List<PublicationDto> matches = service.findByName("Planescape: Adventures in the Multiverse");

        assertEquals(List.of("MPP", "SatO"), matches.stream()
                .map(publication -> publication.id().value())
                .sorted()
                .toList());
    }

    @Test
    void preservesPolymorphicContentsOnlyInInternalProvenance() throws Exception {
        var provenance = catalog.provenanceById("PHB");
        assertTrue(provenance.contents().stream()
                .flatMap(content -> content.headers().stream())
                .anyMatch(TextBookHeaderDte.class::isInstance));
        assertTrue(provenance.contents().stream()
                .flatMap(content -> content.headers().stream())
                .anyMatch(StructuredBookHeaderDte.class::isInstance));

        String json = objectMapper.writeValueAsString(service.findById("PHB"));
        assertFalse(json.contains("contents"));
        assertFalse(json.contains("covers/"));
        assertFalse(json.contains("coverCredit"));
        assertFalse(json.contains("revised"));
        assertFalse(json.contains("aliases"));
        assertFalse(json.contains("parentSource"));
    }

    @Test
    void rejectsUnknownSourceProperties() {
        String unknownField = "{\"book\":[],\"newCollection\":[]}";
        assertThrows(JsonProcessingException.class, () -> strictReader().readValue(unknownField));
    }

    @Test
    void duplicatePublicationCodesFailCatalogConstruction() throws Exception {
        BookDte playerHandbook = findBook("PHB");
        BooksFileDte duplicateBooks = new BooksFileDte(List.of(playerHandbook, playerHandbook));

        assertThrows(
                IllegalStateException.class,
                () -> new PublicationCatalog(duplicateBooks, new PublicationMapper()));
    }

    @Test
    void restEndpointsFilterLookupAndReportMissingRecords() throws Exception {
        mockMvc.perform(get("/api/books").param("source", "XPHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.value").value("XPHB"));

        mockMvc.perform(get("/api/books/PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Player's Handbook (2014)"));

        mockMvc.perform(get("/api/books/by-name/{name}", "Planescape: Adventures in the Multiverse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id.value").exists());

        mockMvc.perform(get("/api/books/unknown"))
                .andExpect(status().isNotFound());
    }

    private com.fasterxml.jackson.databind.ObjectReader strictReader() {
        return objectMapper.readerFor(BooksFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private BookDte findBook(String source) throws Exception {
        try (InputStream input = new ClassPathResource("data/books.json").getInputStream()) {
            return strictReader().<BooksFileDte>readValue(input).book().stream()
                    .filter(book -> book.source().equals(source))
                    .findFirst()
                    .orElseThrow();
        }
    }
}
