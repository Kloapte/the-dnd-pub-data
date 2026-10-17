package com.thedndpub.data.dto.variantrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VariantRuleSummaryDto(
        VariantRuleId id,
        String name,
        SourceIdentity source,
        Integer page,
        String ruleType,
        String rootEntryType,
        Integer sectionCount,
        Integer tableCount) {
    public static VariantRuleSummaryDto from(VariantRuleDefinition definition) {
        return new VariantRuleSummaryDto(
                definition.id(),
                definition.name(),
                definition.source().identity(),
                definition.source().page(),
                definition.ruleType(),
                definition.rootEntryType(),
                definition.sectionCount(),
                definition.tableCount());
    }
}
