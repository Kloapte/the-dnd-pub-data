package com.thedndpub.data.services.corerules;

import com.thedndpub.data.repositories.corerules.ConditionRepository;
import org.springframework.stereotype.Service;

@Service
public class ConditionService extends CoreRulesDomainService {
    public ConditionService(ConditionRepository repository) {
        super(repository);
    }
}
