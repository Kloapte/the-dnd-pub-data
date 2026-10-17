package com.thedndpub.data.dto.deity;

import com.thedndpub.data.dte.deity.DeitiesFileDte;
import com.thedndpub.data.dte.deity.DeityCopyDte;
import com.thedndpub.data.dte.deity.DeityDte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Resolves exact authored deity copy identities without merging descriptive entries. */
public final class DeityCopyResolver {
    public List<ResolvedDeityDte> resolve(DeitiesFileDte source) {
        if (source == null || source.deity().isEmpty()) {
            throw new IllegalStateException("Deity source has no definitions");
        }
        Map<Identity, DeityDte> byIdentity = new HashMap<>();
        for (DeityDte deity : source.deity()) {
            validateIdentity(deity);
            Identity identity = identity(deity.name(), deity.source(), deity.pantheon());
            if (byIdentity.putIfAbsent(identity, deity) != null) {
                throw new IllegalStateException("Duplicate deity identity: " + identity);
            }
        }

        Map<Identity, ResolvedDeityDte> resolved = new HashMap<>();
        for (DeityDte deity : source.deity()) {
            resolve(deity, byIdentity, resolved, new HashSet<>());
        }
        return source.deity().stream()
                .map(deity -> resolved.get(identity(deity.name(), deity.source(), deity.pantheon())))
                .toList();
    }

    private ResolvedDeityDte resolve(
            DeityDte deity,
            Map<Identity, DeityDte> byIdentity,
            Map<Identity, ResolvedDeityDte> resolved,
            Set<Identity> visiting) {
        Identity identity = identity(deity.name(), deity.source(), deity.pantheon());
        ResolvedDeityDte cached = resolved.get(identity);
        if (cached != null) {
            return cached;
        }
        if (!visiting.add(identity)) {
            throw new IllegalStateException("Deity copy cycle detected at " + identity);
        }

        ResolvedDeityDte result;
        DeityCopyDte copy = deity.copy();
        if (copy == null) {
            result = new ResolvedDeityDte(
                    deity, deity.alignment(), deity.domains(), deity.symbol(), List.of(), List.of());
        } else {
            Identity parentIdentity = identity(copy.name(), copy.source(), copy.pantheon());
            DeityDte parentSource = byIdentity.get(parentIdentity);
            if (parentSource == null) {
                throw new IllegalStateException("Deity copy parent not found: " + parentIdentity);
            }
            validateCopyLineage(deity, copy);
            ResolvedDeityDte parent = resolve(parentSource, byIdentity, resolved, visiting);

            List<String> inheritedFields = new ArrayList<>();
            List<String> alignment = choose(
                    deity.alignment(), parent.effectiveAlignment(), "alignment", inheritedFields);
            List<String> domains = choose(
                    deity.domains(), parent.effectiveDomains(), "domains", inheritedFields);
            String symbol = choose(deity.symbol(), parent.effectiveSymbol(), "symbol", inheritedFields);

            List<DeityId> lineage = new ArrayList<>();
            lineage.add(DeityMapper.idFor(parentSource.name(), parentSource.source(), parentSource.pantheon()));
            lineage.addAll(parent.copyLineage());
            result = new ResolvedDeityDte(
                    deity, alignment, domains, symbol, lineage, inheritedFields.stream().distinct().toList());
        }

        resolved.put(identity, result);
        visiting.remove(identity);
        return result;
    }

    private static void validateCopyLineage(DeityDte deity, DeityCopyDte copy) {
        String expected = copy.name() + "|" + copy.pantheon() + "|" + copy.source();
        if (!expected.equals(deity.customExtensionOf())) {
            throw new IllegalStateException("Deity customExtensionOf does not match _copy identity for "
                    + deity.name() + "|" + deity.source());
        }
    }

    private static List<String> choose(
            List<String> authored,
            List<String> inherited,
            String field,
            List<String> inheritedFields) {
        if (authored != null && !authored.isEmpty()) {
            return authored;
        }
        if (inherited != null && !inherited.isEmpty()) {
            inheritedFields.add(field);
            return inherited;
        }
        return List.of();
    }

    private static String choose(
            String authored,
            String inherited,
            String field,
            List<String> inheritedFields) {
        if (authored != null && !authored.isEmpty()) {
            return authored;
        }
        if (inherited != null && !inherited.isEmpty()) {
            inheritedFields.add(field);
            return inherited;
        }
        return null;
    }

    private static void validateIdentity(DeityDte deity) {
        if (deity == null || blank(deity.name()) || blank(deity.source()) || blank(deity.pantheon())) {
            throw new IllegalStateException("Deity identity requires authored name, source, and pantheon");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static Identity identity(String name, String source, String pantheon) {
        return new Identity(source, pantheon, name);
    }

    private record Identity(String source, String pantheon, String name) {
    }
}
