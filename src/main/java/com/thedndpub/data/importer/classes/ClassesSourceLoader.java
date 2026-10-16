package com.thedndpub.data.importer.classes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.classes.ClassRootDte;
import com.thedndpub.data.dte.classes.ClassesFileDte;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/** Explicit import boundary, not a Spring catalog bean or a persistent cache. */
public final class ClassesSourceLoader {
    private final ObjectMapper mapper;
    private final PathMatchingResourcePatternResolver resources;

    public ClassesSourceLoader(ObjectMapper mapper) {
        this(mapper, ClassesSourceLoader.class.getClassLoader());
    }

    public ClassesSourceLoader(ObjectMapper mapper, ClassLoader classLoader) {
        this.mapper = mapper.copy()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
        resources = new PathMatchingResourcePatternResolver(classLoader);
        // Imports own their streams; JVM URL caches must not keep packaged JARs locked after loading.
        resources.setUseCaches(false);
    }

    public ClassSourceCorpus load() throws IOException {
        TreeMap<String, Resource> files = new TreeMap<>();
        for (Resource resource : resources.getResources("classpath*:data/classes/**/*.json")) {
            String url = resource.getURL().toExternalForm();
            int root = url.lastIndexOf("data/classes/");
            String name = url.substring(root);
            Resource previous = files.putIfAbsent(name, resource);
            if (previous != null && !previous.getURL().equals(resource.getURL())) {
                throw new IOException("Duplicate class source resource: " + name);
            }
        }
        if (files.isEmpty()) {
            throw new IOException("No class source resources found under data/classes/");
        }
        List<ClassSourceDocument> documents = new ArrayList<>();
        for (var file : files.entrySet()) {
            var connection = file.getValue().getURL().openConnection();
            connection.setUseCaches(false);
            try (InputStream input = connection.getInputStream()) {
                documents.add(read(file.getKey(), input));
            }
        }
        return new ClassSourceCorpus(documents);
    }

    public ClassSourceDocument read(String resource, InputStream input) throws IOException {
        JsonNode root = mapper.reader()
                .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .readTree(input);
        if (root == null || !root.isObject()) {
            throw new IOException("Class source must be a JSON object: " + resource);
        }
        ClassesFileDte data = mapper.treeToValue(root, ClassesFileDte.class);
        List<ClassSourceRecord> records = new ArrayList<>();
        add(records, resource, "class", data.classes(), root);
        add(records, resource, "subclass", data.subclass(), root);
        add(records, resource, "classFeature", data.classFeature(), root);
        add(records, resource, "subclassFeature", data.subclassFeature(), root);
        return new ClassSourceDocument(resource, data, records);
    }

    private static void add(
            List<ClassSourceRecord> records,
            String resource,
            String collection,
            List<? extends ClassRootDte> roots,
            JsonNode document) {
        if (roots == null) {
            return;
        }
        for (int index = 0; index < roots.size(); index++) {
            JsonNode root = document.get(collection).get(index);
            Set<String> present = new HashSet<>();
            Set<String> nulls = new HashSet<>();
            root.fieldNames().forEachRemaining(present::add);
            root.fields().forEachRemaining(field -> {
                if (field.getValue().isNull()) {
                    nulls.add(field.getKey());
                }
            });
            records.add(new ClassSourceRecord(roots.get(index),
                    new ClassSourceLocation(resource, collection, index), new ClassFieldPresence(present, nulls)));
        }
    }
}
