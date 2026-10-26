package com.thedndpub.data.services.corerules;

import com.thedndpub.data.dto.corerules.CanonicalCoreRulesRecord;
import com.thedndpub.data.repositories.corerules.CoreRulesDomainRepository;

import java.util.List;
import java.util.Optional;

public abstract class CoreRulesDomainService {
    private final CoreRulesDomainRepository repository;

    protected CoreRulesDomainService(CoreRulesDomainRepository repository) {
        this.repository = repository;
    }

    public List<CanonicalCoreRulesRecord> findAll() {
        return repository.findAll();
    }

    public Optional<CanonicalCoreRulesRecord> findBySourceAndSlug(String source, String slug) {
        return repository.findBySourceAndSlug(source, slug);
    }

    public Optional<byte[]> findAllJson() {
        return repository.findAllJson();
    }

    public Optional<byte[]> findJsonBySourceAndSlug(String source, String slug) {
        return repository.findJsonBySourceAndSlug(source, slug);
    }
}
