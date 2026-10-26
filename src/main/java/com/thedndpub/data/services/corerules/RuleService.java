package com.thedndpub.data.services.corerules;

import com.thedndpub.data.repositories.corerules.RuleRepository;
import org.springframework.stereotype.Service;

@Service
public class RuleService extends CoreRulesDomainService {
    public RuleService(RuleRepository repository) {
        super(repository);
    }
}
