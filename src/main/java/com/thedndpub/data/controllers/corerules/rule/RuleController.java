package com.thedndpub.data.controllers.corerules.rule;

import com.thedndpub.data.controllers.corerules.CoreRulesDomainController;
import com.thedndpub.data.services.corerules.RuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core-rules/rule")
@Tag(name = "Rule", description = "Read-only canonical Rule catalog")
public class RuleController extends CoreRulesDomainController {
    public RuleController(RuleService service) {
        super(service, "rule");
    }
}
