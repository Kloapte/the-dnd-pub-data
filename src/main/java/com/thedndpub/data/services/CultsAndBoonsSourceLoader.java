package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.cultboon.CultsAndBoonsFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/** Loads the shared source document once; Cult and Boon indexes remain separate. */
@Component
public final class CultsAndBoonsSourceLoader {
    private static final String RESOURCE_PATH = "data/cultsboons.json";

    private final CultsAndBoonsFileDte source;

    @Autowired
    public CultsAndBoonsSourceLoader(ObjectMapper objectMapper) {
        this(load(objectMapper));
    }

    public CultsAndBoonsSourceLoader(CultsAndBoonsFileDte source) {
        this.source = Objects.requireNonNull(source, "source");
        if (source.cult() == null || source.boon() == null) {
            throw new IllegalStateException("Cults and Boons source must contain both root collections");
        }
        if (source.cult().isEmpty() || source.boon().isEmpty()) {
            throw new IllegalStateException("Cults and Boons source collections must not be empty");
        }
    }

    public CultsAndBoonsFileDte source() {
        return source;
    }

    private static CultsAndBoonsFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(CultsAndBoonsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Cults and Boons resource " + RESOURCE_PATH, exception);
        }
    }
}
