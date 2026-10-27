package com.thedndpub.data.repositories.disease;

import com.thedndpub.data.dto.disease.DiseaseRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class DiseaseRepository {
    private final List<DiseaseRecord> diseases;

    public DiseaseRepository(
            ObjectMapper objectMapper,
            @Value("${dndpub.diseases.dataset:classpath:normalized/core-rules/diseases.json}") Resource dataset) {
        this.diseases = loadDiseases(objectMapper, dataset);
    }

    public List<DiseaseRecord> findAll() {
        return diseases;
    }

    private static List<DiseaseRecord> loadDiseases(ObjectMapper objectMapper, Resource dataset) {
        try (InputStream inputStream = dataset.getInputStream()) {
            DiseaseRecord[] parsed = objectMapper.readValue(inputStream, DiseaseRecord[].class);
            if (parsed == null || parsed.length == 0) {
                throw new IllegalArgumentException("The Disease dataset must contain at least one Disease");
            }
            List<DiseaseRecord> diseases = List.of(parsed);
            validateUniqueIds(diseases);
            return diseases;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Disease dataset from " + dataset.getDescription(),
                    exception);
        }
    }

    private static void validateUniqueIds(List<DiseaseRecord> diseases) {
        Set<String> ids = new HashSet<>();
        for (DiseaseRecord disease : diseases) {
            if (!ids.add(disease.id())) {
                throw new IllegalStateException("Duplicate Disease id '" + disease.id() + "'");
            }
        }
    }
}
