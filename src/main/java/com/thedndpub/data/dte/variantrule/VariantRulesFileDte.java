package com.thedndpub.data.dte.variantrule;

import java.util.List;

public record VariantRulesFileDte(List<VariantRuleDte> variantrule) {
    public VariantRulesFileDte {
        variantrule = variantrule == null ? List.of() : List.copyOf(variantrule);
    }
}
