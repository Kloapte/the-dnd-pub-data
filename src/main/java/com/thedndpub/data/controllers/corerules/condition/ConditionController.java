package com.thedndpub.data.controllers.corerules.condition;

import com.thedndpub.data.controllers.corerules.CoreRulesDomainController;
import com.thedndpub.data.services.corerules.ConditionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core-rules/condition")
@Tag(name = "Condition", description = "Read-only canonical Condition catalog")
public class ConditionController extends CoreRulesDomainController {
    public ConditionController(ConditionService service) {
        super(service, "condition");
    }
}
