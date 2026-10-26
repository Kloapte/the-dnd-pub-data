package com.thedndpub.data.services;

import com.thedndpub.data.dto.corerules.CanonicalCoreRulesRecord;
import com.thedndpub.data.dto.corerules.CanonicalRecordIdentity;
import com.thedndpub.data.dto.corerules.CanonicalReference;
import com.thedndpub.data.dto.corerules.ReferenceResolution;
import com.thedndpub.data.repositories.CoreRulesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoreRulesCatalog {
    private final CoreRulesRepository repository;

    public CoreRulesCatalog(CoreRulesRepository repository) {
        this.repository = repository;
    }

    public List<CanonicalCoreRulesRecord> findAll() {
        return repository.findAll();
    }

    public List<CanonicalCoreRulesRecord> findAllByDomain(String domain) {
        return repository.findAllByDomain(domain);
    }

    public Optional<byte[]> findDomainJson(String domain) {
        return repository.findDomainJson(domain);
    }

    public Optional<CanonicalCoreRulesRecord> findById(String id) {
        return repository.findById(id);
    }

    public Optional<byte[]> findCanonicalJson(String id) {
        return repository.findCanonicalJson(id);
    }

    public ReferenceResolution resolve(CanonicalReference reference) {
        CanonicalRecordIdentity canonicalTarget = repository.findById(reference.id())
                .map(CanonicalCoreRulesRecord::identity)
                .orElse(null);
        if (canonicalTarget != null) {
            return new ReferenceResolution(reference,
                    ReferenceResolution.State.CANONICAL_TARGET_AVAILABLE, canonicalTarget);
        }
        if (repository.containsSourceIdentity(reference.id())) {
            return new ReferenceResolution(reference,
                    ReferenceResolution.State.SOURCE_KNOWN_WITHOUT_CANONICAL_OUTPUT, null);
        }
        return new ReferenceResolution(reference, ReferenceResolution.State.SOURCE_IDENTITY_UNKNOWN, null);
    }

    public boolean containsDomain(String domain) {
        return repository.containsDomain(domain);
    }

    public int size() {
        return repository.size();
    }
}
