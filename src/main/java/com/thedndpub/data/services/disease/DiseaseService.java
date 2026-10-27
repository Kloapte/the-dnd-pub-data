package com.thedndpub.data.services.disease;

import com.thedndpub.data.dto.disease.DiseaseRecord;
import com.thedndpub.data.repositories.disease.DiseaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiseaseService {
    private final DiseaseRepository diseaseRepository;

    public DiseaseService(DiseaseRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    public List<DiseaseRecord> getAll() {
        return diseaseRepository.findAll();
    }

    public Optional<DiseaseRecord> getById(String id) {
        return diseaseRepository.findAll().stream()
                .filter(disease -> disease.id().equals(id))
                .findFirst();
    }

    public List<DiseaseRecord> getByName(String name) {
        return diseaseRepository.findAll().stream()
                .filter(disease -> disease.name().equals(name))
                .toList();
    }
}
