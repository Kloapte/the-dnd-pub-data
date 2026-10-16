package com.thedndpub.data.dto.magicvariant;

public record MagicVariantCatalogStatistics(
        int rootRecordCount,
        int genericRuleCount,
        int itemSpecificTransformationCount,
        long potentialGenericItemCombinations,
        long potentialItemSpecificMatches,
        int duplicateGeneratedIdentityGroups,
        long duplicateGeneratedIdentityOccurrences,
        int generatedIdentityGroupsCollidingWithItems,
        long generatedIdentityCollisionsWithItems,
        long overlappingRuleApplicationPairs,
        long conflictingRuleApplicationPairs,
        int genericRulesWithoutEligibleItems,
        int unresolvedBaseItemReferences) {
}
