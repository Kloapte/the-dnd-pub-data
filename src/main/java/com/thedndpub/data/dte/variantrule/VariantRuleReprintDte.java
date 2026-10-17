package com.thedndpub.data.dte.variantrule;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = VariantRuleReprintDeserializer.class)
public sealed interface VariantRuleReprintDte
        permits VariantRuleReprintTokenDte, VariantRuleReprintUidDte {
}
