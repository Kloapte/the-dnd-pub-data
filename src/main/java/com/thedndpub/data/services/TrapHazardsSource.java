package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.traphazard.TrapHazardsFileDte;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public final class TrapHazardsSource {
    private static final String RESOURCE = "data/trapshazards.json";
    private final TrapHazardsFileDte file;

    public TrapHazardsSource(ObjectMapper mapper) {
        try (InputStream input = new ClassPathResource(RESOURCE).getInputStream()) {
            file = mapper.readerFor(TrapHazardsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load trap and hazard resource " + RESOURCE, exception);
        }
    }

    public TrapHazardsFileDte file() {
        return file;
    }
}
