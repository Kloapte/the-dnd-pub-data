package com.thedndpub.data.services;

import com.thedndpub.data.dto.variantrule.VariantRuleDefinition;
import com.thedndpub.data.dto.variantrule.VariantRuleDetailDto;
import com.thedndpub.data.dto.variantrule.VariantRuleSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class VariantRulesService {
    private final VariantRuleCatalog catalog;
    private final PublicReferenceProjection references;

    public VariantRulesService(VariantRuleCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<VariantRuleSummaryDto> find(String name, String source, String ruleType) {
        String nameFilter = normalize(name);
        String sourceFilter = normalize(source);
        String typeFilter = normalize(ruleType);
        return catalog.summaries().stream()
                .filter(value -> nameFilter == null
                        || value.name().toLowerCase(Locale.ROOT).contains(nameFilter))
                .filter(value -> sourceFilter == null
                        || value.source().code().toLowerCase(Locale.ROOT).equals(sourceFilter))
                .filter(value -> typeFilter == null
                        || value.ruleType() != null && value.ruleType().toLowerCase(Locale.ROOT).equals(typeFilter))
                .toList();
    }

    public VariantRuleDetailDto byId(String id) {
        VariantRuleDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new VariantRuleNotFoundException("No Variant Rule found with ID '" + id + "'");
        }
        return VariantRuleDetailDto.from(definition)
                .withCrossCatalogReferences(references.forOwner("variant-rule", id));
    }

    public List<VariantRuleDetailDto> byName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Variant Rule name is required");
        }
        String sourceFilter = normalize(source);
        List<VariantRuleDetailDto> matches = catalog.findByName(name).stream()
                .filter(value -> sourceFilter == null
                        || value.source().identity().code().toLowerCase(Locale.ROOT).equals(sourceFilter))
                .map(value -> VariantRuleDetailDto.from(value)
                        .withCrossCatalogReferences(references.forOwner("variant-rule", value.id().value())))
                .toList();
        if (matches.isEmpty()) {
            throw new VariantRuleNotFoundException("No Variant Rule found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
