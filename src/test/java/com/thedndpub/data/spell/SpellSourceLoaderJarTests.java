package com.thedndpub.data.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.importer.spell.SpellSourceLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpellSourceLoaderJarTests {
    @TempDir
    Path temporaryDirectory;

    @Test
    void discoversSpellResourcesInsidePackagedJar() throws Exception {
        Path jarPath = temporaryDirectory.resolve("spell-resources.jar");
        writeJar(jarPath);

        boolean originalJarCacheSetting = URLConnection.getDefaultUseCaches("jar");
        URLConnection.setDefaultUseCaches("jar", false);
        try {
            try (URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{jarPath.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
                var resolver = new PathMatchingResourcePatternResolver(classLoader);
                var corpus = new SpellSourceLoader(new ObjectMapper(), resolver).load();

                assertEquals(1, corpus.documents().size());
                assertEquals("spells-jar.json", corpus.documents().getFirst().resourceName());
                assertEquals("Jar Spell", corpus.spells().getFirst().name());
            }
        } finally {
            URLConnection.setDefaultUseCaches("jar", originalJarCacheSetting);
        }
    }

    private void writeJar(Path jarPath) throws Exception {
        try (var output = new JarOutputStream(Files.newOutputStream(jarPath))) {
            addEntry(output, "data/");
            addEntry(output, "data/spells/");
            addEntry(output, "data/spells/spells-jar.json");
            output.write("{\"spell\":[{\"name\":\"Jar Spell\",\"source\":\"PHB\"}]}"
                    .getBytes(StandardCharsets.UTF_8));
            output.closeEntry();
        }
    }

    private void addEntry(JarOutputStream output, String name) throws Exception {
        output.putNextEntry(new JarEntry(name));
        if (name.endsWith("/")) {
            output.closeEntry();
        }
    }
}
