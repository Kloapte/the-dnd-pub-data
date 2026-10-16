package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.importer.classes.ClassSourceCorpus;
import com.thedndpub.data.importer.classes.ClassesSourceLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ClassesResourceLoadingTests {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadsCompleteCorpusFromAnIsolatedJarClasspath() throws Exception {
        var source = ClassTestSources.fullCorpus();
        Path jar = writeJar("classes.jar", source, false);
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, null)) {
            var packaged = new ClassesSourceLoader(new ObjectMapper(), classLoader).load();
            assertEquals(source, packaged);
            assertEquals(2536, packaged.records().size());
        }
    }

    @Test
    void discoversNestedFilesWithoutAHardcodedManifest() throws Exception {
        var source = ClassTestSources.fullCorpus();
        Path jar = writeJar("nested.jar", source, true);
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, null)) {
            var packaged = new ClassesSourceLoader(new ObjectMapper(), classLoader).load();
            assertEquals(16, packaged.documents().size());
            assertEquals(2537, packaged.records().size());
            assertTrue(packaged.documents().stream().anyMatch(document -> document.resource().contains("nested/")));
        }
    }

    @Test
    void rejectsConflictingClasspathResourcesAndAnEmptyCorpus() throws Exception {
        var source = ClassTestSources.fullCorpus();
        Path first = writeJar("first.jar", source, false);
        Path second = writeJar("second.jar", source, false);
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{first.toUri().toURL(), second.toUri().toURL()}, null)) {
            assertThrows(IOException.class, () -> new ClassesSourceLoader(new ObjectMapper(), classLoader).load());
        }
        try (URLClassLoader classLoader = new URLClassLoader(new URL[0], null)) {
            assertThrows(IOException.class, () -> new ClassesSourceLoader(new ObjectMapper(), classLoader).load());
        }
    }

    private Path writeJar(String filename, ClassSourceCorpus source, boolean nested) throws Exception {
        Path target = temporaryDirectory.resolve(filename);
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(target))) {
            for (String directory : new String[]{"data/", "data/classes/"}) {
                jar.putNextEntry(new JarEntry(directory));
                jar.closeEntry();
            }
            for (var document : source.documents()) {
                jar.putNextEntry(new JarEntry(document.resource()));
                try (var input = new ClassPathResource(document.resource()).getInputStream()) {
                    input.transferTo(jar);
                }
                jar.closeEntry();
            }
            if (nested) {
                jar.putNextEntry(new JarEntry("data/classes/nested/"));
                jar.closeEntry();
                jar.putNextEntry(new JarEntry("data/classes/nested/extra.json"));
                jar.write("{\"class\":[{\"name\":\"Nested\",\"source\":\"PHB\"}]}".getBytes(StandardCharsets.UTF_8));
                jar.closeEntry();
            }
        }
        return target;
    }
}
