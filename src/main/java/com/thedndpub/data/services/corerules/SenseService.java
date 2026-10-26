package com.thedndpub.data.services.corerules;

import com.thedndpub.data.dto.corerules.sense.SenseRecord;
import com.thedndpub.data.repositories.corerules.SenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SenseService extends CoreRulesDomainService {
    private final SenseRepository senseRepository;

    public SenseService(SenseRepository repository) {
        super(repository);
        this.senseRepository = repository;
    }

    public List<SenseRecord> findAllSenses() {
        return senseRepository.findAllSenses();
    }

    public Optional<SenseRecord> findSense(String source, String slug) {
        return senseRepository.findSense(source, slug);
    }
}
