package com.thedndpub.data.repositories.corerules;

import com.thedndpub.data.dto.corerules.CanonicalCoreRulesRecord;
import com.thedndpub.data.repositories.CoreRulesRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public abstract class CoreRulesDomainRepository {
    private final CoreRulesRepository catalogRepository;
    private final ObjectMapper objectMapper;
    private final String domain;

    protected CoreRulesDomainRepository(
            CoreRulesRepository catalogRepository,
            ObjectMapper objectMapper,
            String domain) {
        this.catalogRepository = catalogRepository;
        this.objectMapper = objectMapper;
        this.domain = domain;
    }

    protected <T> List<T> readAllModels(Class<T[]> arrayType) {
        byte[] json = catalogRepository.findDomainJson(domain)
                .orElseThrow(() -> new IllegalStateException("Canonical domain resource is unavailable: " + domain));
        try {
            return List.of(objectMapper.readValue(json, arrayType));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to deserialize canonical " + domain + " models", exception);
        }
    }

    protected <T> Optional<T> readModel(String source, String slug, Class<T> recordType) {
        byte[] json = catalogRepository.findCanonicalJson(domain + ":" + source + ":" + slug).orElse(null);
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, recordType));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to deserialize canonical " + domain + " model", exception);
        }
    }

    public List<CanonicalCoreRulesRecord> findAll() {
        return catalogRepository.findAllByDomain(domain);
    }

    public Optional<CanonicalCoreRulesRecord> findBySourceAndSlug(String source, String slug) {
        return catalogRepository.findById(domain + ":" + source + ":" + slug);
    }

    public Optional<byte[]> findAllJson() {
        return catalogRepository.findDomainJson(domain);
    }

    public Optional<byte[]> findJsonBySourceAndSlug(String source, String slug) {
        return catalogRepository.findCanonicalJson(domain + ":" + source + ":" + slug);
    }
}
