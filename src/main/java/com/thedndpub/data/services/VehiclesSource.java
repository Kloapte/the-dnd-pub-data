package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.vehicle.VehicleFileDte;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public final class VehiclesSource {
    private static final String RESOURCE = "data/vehicles.json";
    private final VehicleFileDte file;

    public VehiclesSource(ObjectMapper mapper) {
        try (InputStream input = new ClassPathResource(RESOURCE).getInputStream()) {
            file = mapper.readerFor(VehicleFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load vehicle resource " + RESOURCE, exception);
        }
    }

    public VehicleFileDte file() {
        return file;
    }
}
