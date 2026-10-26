package com.thedndpub.data.controllers.corerules.action;

import com.thedndpub.data.controllers.corerules.CoreRulesDomainController;
import com.thedndpub.data.services.corerules.ActionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core-rules/action")
@Tag(name = "Action", description = "Read-only canonical Action catalog")
public class ActionController extends CoreRulesDomainController {
    public ActionController(ActionService service) {
        super(service, "action");
    }
}
