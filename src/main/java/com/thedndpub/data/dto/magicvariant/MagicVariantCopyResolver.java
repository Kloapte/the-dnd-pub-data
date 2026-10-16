package com.thedndpub.data.dto.magicvariant;

import com.thedndpub.data.dto.item.ItemDefinition;
import com.thedndpub.data.dte.magicvariant.MagicVariantCopyOperationDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantDte;
import com.thedndpub.data.services.ItemCatalog;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public final class MagicVariantCopyResolver {
    public List<ResolvedMagicVariantDte> resolve(List<MagicVariantDte> variants, ItemCatalog itemCatalog) {
        Map<String, MagicVariantDte> byIdentity = new HashMap<>();
        for (MagicVariantDte variant : variants) {
            String sourceCode = sourceCode(variant);
            String identity = identity(variant.name(), sourceCode);
            MagicVariantDte previous = byIdentity.putIfAbsent(identity, variant);
            if (previous != null) {
                throw new IllegalStateException("Duplicate magic variant identity: " + variant.name()
                        + "|" + sourceCode);
            }
        }

        Map<String, ResolvedMagicVariantDte> resolved = new HashMap<>();
        Set<String> resolving = new HashSet<>();
        List<ResolvedMagicVariantDte> result = new ArrayList<>();
        for (MagicVariantDte variant : variants) {
            result.add(resolve(variant, byIdentity, itemCatalog, resolved, resolving));
        }
        return List.copyOf(result);
    }

    private static ResolvedMagicVariantDte resolve(
            MagicVariantDte variant,
            Map<String, MagicVariantDte> byIdentity,
            ItemCatalog itemCatalog,
            Map<String, ResolvedMagicVariantDte> resolved,
            Set<String> resolving) {
        String sourceCode = sourceCode(variant);
        String identity = identity(variant.name(), sourceCode);
        ResolvedMagicVariantDte cached = resolved.get(identity);
        if (cached != null) {
            return cached;
        }
        if (!resolving.add(identity)) {
            throw new IllegalStateException("Magic variant copy cycle detected at " + variant.name()
                    + "|" + sourceCode);
        }

        String baseItemName = null;
        String baseItemSource = null;
        String baseItemId = null;
        List<String> lineage = new ArrayList<>();
        List<MagicVariantCopyOperationDte> operations = new ArrayList<>();
        boolean copyChainResolved = variant.copy() == null;
        boolean baseItemResolved = false;

        if (variant.copy() != null) {
            String parentName = variant.copy().name();
            String parentSource = variant.copy().source();
            MagicVariantDte parentVariant = byIdentity.get(identity(parentName, parentSource));
            if (parentVariant != null) {
                ResolvedMagicVariantDte parent = resolve(
                        parentVariant, byIdentity, itemCatalog, resolved, resolving);
                baseItemName = parent.baseItemName() == null ? parentName : parent.baseItemName();
                baseItemSource = parent.baseItemSource() == null ? parentSource : parent.baseItemSource();
                baseItemId = parent.baseItemId();
                if (baseItemId == null) {
                    String resolvedBaseItemSource = baseItemSource;
                    ItemDefinition parentItem = itemCatalog.findByName(baseItemName).stream()
                            .filter(item -> item.source().identity().code().equalsIgnoreCase(resolvedBaseItemSource))
                            .findFirst()
                            .orElse(null);
                    if (parentItem != null) {
                        baseItemId = parentItem.id();
                    }
                }
                baseItemResolved = baseItemId != null;
                copyChainResolved = true;
                lineage.addAll(parent.copyLineage());
                operations.addAll(parent.resolvedOperations());
            } else {
                ItemDefinition parentItem = itemCatalog.findByName(parentName).stream()
                        .filter(item -> item.source().identity().code().equalsIgnoreCase(parentSource))
                        .findFirst()
                        .orElse(null);
                baseItemName = parentName;
                baseItemSource = parentSource;
                if (parentItem != null) {
                    baseItemId = parentItem.id();
                    baseItemResolved = true;
                    copyChainResolved = true;
                }
            }
            lineage.add(identityPart(parentName, parentSource));
            operations.addAll(variant.copy().operations());
        }

        lineage.add(identityPart(variant.name(), sourceCode));
        resolving.remove(identity);
        ResolvedMagicVariantDte result = new ResolvedMagicVariantDte(
                variant,
                sourceCode,
                baseItemName,
                baseItemSource,
                baseItemId,
                lineage,
                operations,
                copyChainResolved,
                baseItemResolved);
        resolved.put(identity, result);
        return result;
    }

    public static String sourceCode(MagicVariantDte variant) {
        if (variant.source() != null && !variant.source().isBlank()) {
            return variant.source();
        }
        if (variant.type() == null || variant.type().isBlank()) {
            throw new IllegalStateException("Magic variant has no source identity: " + variant.name());
        }
        String[] parts = variant.type().split("\\|", -1);
        if (parts.length != 2 || parts[1].isBlank()) {
            throw new IllegalStateException("Magic variant type must include a source code: " + variant.type());
        }
        return parts[1];
    }

    public static String identity(String name, String source) {
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static String identityPart(String name, String source) {
        return name + "|" + source;
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
