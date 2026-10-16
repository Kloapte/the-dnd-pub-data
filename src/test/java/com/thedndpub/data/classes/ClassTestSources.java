package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.importer.classes.ClassSourceCorpus;
import com.thedndpub.data.importer.classes.ClassesSourceLoader;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class ClassTestSources {
    private ClassTestSources() {
    }

    static ClassSourceCorpus read(String json) throws Exception {
        var loader = new ClassesSourceLoader(new ObjectMapper());
        var document = loader.read("fixture.json",
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        return new ClassSourceCorpus(List.of(document));
    }

    static ClassSourceCorpus fullCorpus() throws Exception {
        return new ClassesSourceLoader(new ObjectMapper()).load();
    }
}
