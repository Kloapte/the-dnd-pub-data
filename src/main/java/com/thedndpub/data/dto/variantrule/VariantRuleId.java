package com.thedndpub.data.dto.variantrule;

public record VariantRuleId(String value) {
    public VariantRuleId {
        if (value == null || !value.startsWith("variant-rule:")) {
            throw new IllegalArgumentException("Variant Rule ID must start with variant-rule:");
        }
    }
}
