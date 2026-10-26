package com.thedndpub.data.services.corerules;

import com.thedndpub.data.dto.corerules.status.StatusRecord;
import com.thedndpub.data.repositories.corerules.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService extends CoreRulesDomainService {
    private final StatusRepository statusRepository;

    public StatusService(StatusRepository repository) {
        super(repository);
        this.statusRepository = repository;
    }

    public List<StatusRecord> findAllStatuses() {
        return statusRepository.findAllStatuses();
    }

    public Optional<StatusRecord> findStatus(String source, String slug) {
        return statusRepository.findStatus(source, slug);
    }
}
