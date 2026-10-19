package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.ModelChecks;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/** Startup-only selection of explicitly supported owners; independent of publication and benchmark data. */
public final class SpellMechanicsExtractionService {
    private final ObjectMapper mapper;
    private final ResourceLoader resources;
    private final SpellMechanicsPatternRegistry patterns = new SpellMechanicsPatternRegistry(List.of(
            new ShieldMechanicsExtractor(), new PhbSpellMechanicsExtractor()));

    public SpellMechanicsExtractionService(ObjectMapper mapper) {
        this(mapper, new DefaultResourceLoader());
    }

    public SpellMechanicsExtractionService(ObjectMapper mapper, ResourceLoader resources) {
        this.mapper = ModelChecks.required(mapper, "mapper");
        this.resources = ModelChecks.required(resources, "resources");
    }

    public List<MechanicsDocument> extract(
            List<MechanicsDocument> unattempted,
            List<SpellDefinition> definitions,
            List<SpellMechanicsSourceOwner> manifest) {
        Map<SpellId, SpellDefinition> owners = new HashMap<>();
        for (SpellDefinition definition : definitions) {
            ModelChecks.require(owners.putIfAbsent(definition.id(), definition) == null, "Duplicate extraction owner");
        }
        Map<SpellId, SpellMechanicsSourceOwner> sourceOwners = new HashMap<>();
        for (SpellMechanicsSourceOwner source : manifest) {
            ModelChecks.require(sourceOwners.putIfAbsent(source.id(), source) == null, "Duplicate extraction source owner");
        }
        ModelChecks.require(sourceOwners.keySet().equals(owners.keySet()), "Extraction source owner set mismatch");
        var seen = new HashSet<SpellId>();
        long expectedPhbOwners = definitions.stream().filter(definition ->
                patterns.find(definition) instanceof PhbSpellMechanicsExtractor).count();
        Map<String, byte[]> sourceBytesByResource = new HashMap<>();
        List<MechanicsDocument> result = new ArrayList<>();
        int processedShieldOwners = 0;
        int processedPhbOwners = 0;
        for (MechanicsDocument document : unattempted) {
            SpellId id = new SpellId(document.owner().id());
            ModelChecks.require(owners.containsKey(id) && seen.add(id), "Unknown or duplicate extraction document");
            SpellDefinition definition = owners.get(id);
            SpellMechanicsPattern pattern = patterns.find(definition);
            if (pattern == null) {
                result.add(document);
                continue;
            }
            SpellMechanicsSourceOwner source = sourceOwners.get(id);
            if (pattern instanceof ShieldMechanicsExtractor) {
                ShieldMechanicsEdition.forId(id).validatePinned(source);
                processedShieldOwners++;
            } else {
                ShieldMechanicsEdition.PHB.validateSourceRevision(source);
                processedPhbOwners++;
            }
            byte[] bytes = sourceBytesByResource.computeIfAbsent(source.revision().resourcePath(), resourcePath -> {
                try {
                    return resources.getResource("classpath:" + resourcePath).getContentAsByteArray();
                } catch (IOException exception) {
                    throw new IllegalStateException("Cannot load pinned Spell evidence resource", exception);
                }
            });
            var evidence = new SpellMechanicsEvidenceSource(definition, source, bytes, mapper);
            result.add(patterns.extract(definition, evidence, document));
        }
        ModelChecks.require(seen.equals(owners.keySet()), "Missing extraction documents");
        ModelChecks.require(processedShieldOwners == ShieldMechanicsEdition.values().length,
                "The pinned Shield pilot requires both authoritative source owners");
        ModelChecks.require(processedPhbOwners == expectedPhbOwners && processedPhbOwners >= 21,
                "The PHB pattern set requires every explicitly supported owner");
        result.sort(Comparator.comparing(document -> document.owner().id()));
        return List.copyOf(result);
    }
}
