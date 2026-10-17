package com.thedndpub.data.services;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellSchool;
import com.thedndpub.data.dto.spell.api.SpellDto;
import com.thedndpub.data.dto.spell.api.SpellPublicMapper;
import com.thedndpub.data.dto.spell.api.SpellSummaryDto;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

@Service
public final class SpellsService {
    private final SpellCatalog catalog;
    private final SpellPublicMapper mapper;

    public SpellsService(SpellCatalog catalog, SpellPublicMapper mapper) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    public List<SpellSummaryDto> findSpells(
            String name,
            String source,
            Integer level,
            String school,
            Boolean ritual,
            Boolean concentration,
            String damageType) {
        if (level != null && level < 0) {
            throw new IllegalArgumentException("Spell level must be zero or greater");
        }
        SpellSchool schoolFilter = normalize(school) == null ? null : SpellSchool.fromCodeOrName(school);

        Predicate<SpellDefinition> matches = spell -> contains(spell.identity().name(), name)
                && equalsIgnoreCase(spell.source().identity().code(), source)
                && (level == null || level.equals(spell.level()))
                && (schoolFilter == null || SpellSchool.fromCode(spell.school()) == schoolFilter)
                && (ritual == null || ritual.equals(spell.ritual()))
                && (concentration == null || concentration.equals(concentration(spell)))
                && (normalize(damageType) == null
                    || spell.mechanics().damageInflicted().stream()
                        .anyMatch(type -> normalize(type).equals(normalize(damageType))));

        return catalog.findAll().stream()
                .filter(matches)
                .map(mapper::summary)
                .toList();
    }

    public SpellDto findById(String id) {
        SpellDefinition spell = catalog.findById(id);
        if (spell == null) {
            throw new SpellNotFoundException("No spell found with ID '" + id + "'");
        }
        return mapper.detail(spell);
    }

    public List<SpellDto> findByName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Spell name is required");
        }
        List<SpellDto> matches = catalog.findByName(name).stream()
                .filter(spell -> source == null || equalsIgnoreCase(spell.source().identity().code(), source))
                .map(mapper::detail)
                .toList();
        if (matches.isEmpty()) {
            throw new SpellNotFoundException("No spell found with name '" + name + "'");
        }
        return matches;
    }

    private Boolean concentration(SpellDefinition spell) {
        if (spell.durations().stream().anyMatch(duration -> Boolean.TRUE.equals(duration.concentration()))) {
            return true;
        }
        if (!spell.durations().isEmpty()
                && spell.durations().stream().allMatch(duration -> Boolean.FALSE.equals(duration.concentration()))) {
            return false;
        }
        return null;
    }

    private static boolean contains(String value, String filter) {
        String normalizedFilter = normalize(filter);
        return normalizedFilter == null || normalize(value).contains(normalizedFilter);
    }

    private static boolean equalsIgnoreCase(String value, String filter) {
        String normalizedFilter = normalize(filter);
        return normalizedFilter == null || normalize(value).equals(normalizedFilter);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
