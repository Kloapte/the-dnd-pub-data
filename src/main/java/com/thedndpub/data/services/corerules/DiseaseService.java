package com.thedndpub.data.services.corerules;

import com.thedndpub.data.repositories.corerules.DiseaseRepository;
import org.springframework.stereotype.Service;

@Service
public class DiseaseService extends CoreRulesDomainService {
    public DiseaseService(DiseaseRepository repository) {
        super(repository);
    }
}
