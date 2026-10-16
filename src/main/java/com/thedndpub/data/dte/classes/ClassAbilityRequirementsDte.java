package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ClassAbilityRequirementsDte(
        Integer str,
        Integer dex,
        Integer con,
        @JsonProperty("int") Integer intelligence,
        Integer wis,
        Integer cha,
        List<ClassAbilityRequirementsDte> or) {
    public ClassAbilityRequirementsDte {
        or = ClassDteCollections.list(or);
    }
}
