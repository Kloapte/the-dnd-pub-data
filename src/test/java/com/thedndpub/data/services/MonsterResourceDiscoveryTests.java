package com.thedndpub.data.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonsterResourceDiscoveryTests {
    @TempDir
    Path temporaryDirectory;

    @Test
    void discoversMonsterFilesInsideJarResources() throws Exception {
        Path jarPath = temporaryDirectory.resolve("test-monsters.jar");
        try (JarOutputStream jarOutput = new JarOutputStream(java.nio.file.Files.newOutputStream(jarPath))) {
            jarOutput.putNextEntry(new JarEntry("data/"));
            jarOutput.closeEntry();
            jarOutput.putNextEntry(new JarEntry("data/monsters/"));
            jarOutput.closeEntry();
            jarOutput.putNextEntry(new JarEntry("data/monsters/bestiary-test.json"));
            jarOutput.write("{\"monster\":[]}".getBytes(StandardCharsets.UTF_8));
            jarOutput.closeEntry();
        }

        URL jarUrl = jarPath.toUri().toURL();
        URLConnection.setDefaultUseCaches("jar", false);
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, null)) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
            Resource[] resources = resolver.getResources("classpath*:data/monsters/bestiary-*.json");

            assertEquals(1, resources.length);
            try (InputStream input = resources[0].getInputStream()) {
                assertEquals("{\"monster\":[]}", new String(input.readAllBytes(), StandardCharsets.UTF_8));
            }
        } finally {
            URLConnection.setDefaultUseCaches("jar", true);
        }
    }
}
