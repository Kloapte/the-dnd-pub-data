package com.thedndpub.data.controllers.corerules.disease;

import com.thedndpub.data.controllers.corerules.CoreRulesDomainController;
import com.thedndpub.data.services.corerules.DiseaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core-rules/disease")
@Tag(name = "Disease", description = "Read-only canonical Disease catalog")
public class DiseaseController extends CoreRulesDomainController {
    public DiseaseController(DiseaseService service) {
        super(service, "disease");
    }
}
