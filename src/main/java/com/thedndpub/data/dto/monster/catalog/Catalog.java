package com.thedndpub.data.dto.monster.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.monster.identity.RulesAvailability;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Catalog(Set<String> environments, List<GearReference> gear, List<String> attachedItems,
                      Set<String> treasure, Boolean familiar, RulesAvailability rulesAvailability,
                      Boolean reprinted) {
}
