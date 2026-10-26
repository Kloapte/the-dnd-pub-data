package com.thedndpub.data.services.corerules;

import com.thedndpub.data.repositories.corerules.ActionRepository;
import org.springframework.stereotype.Service;

@Service
public class ActionService extends CoreRulesDomainService {
    public ActionService(ActionRepository repository) {
        super(repository);
    }
}
