package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsSourceContent;
import com.thedndpub.data.mechanics.model.MechanicsSourceEvidenceVerifier;
import com.thedndpub.data.mechanics.model.MechanicsSourceOwnerPointer;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.CatalogDomain;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
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
    private final SpellMechanicsPatternRegistry patterns = SpellMechanicsPatternRegistry.standard();

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

        Map<SpellId, SpellMechanicsPattern> supportedPatterns = new HashMap<>();
        for (SpellDefinition definition : definitions) {
            SpellMechanicsPattern pattern = patterns.find(definition);
            if (pattern != null) {
                supportedPatterns.put(definition.id(), pattern);
            }
        }
        for (ShieldMechanicsEdition edition : ShieldMechanicsEdition.values()) {
            SpellId ownerId = new SpellId(edition.ownerId());
            ModelChecks.require(supportedPatterns.containsKey(ownerId),
                    "Missing supported Shield source owner " + ownerId.value());
        }

        var seen = new HashSet<SpellId>();
        var extractedSupportedOwners = new HashSet<SpellId>();
        Map<String, byte[]> sourceBytesByResource = new HashMap<>();
        List<MechanicsDocument> result = new ArrayList<>();
        for (MechanicsDocument document : unattempted) {
            SpellId id = new SpellId(document.owner().id());
            ModelChecks.require(owners.containsKey(id) && seen.add(id), "Unknown or duplicate extraction document");
            SpellDefinition definition = owners.get(id);
            SpellMechanicsPattern pattern = supportedPatterns.get(id);
            if (pattern == null) {
                result.add(document);
                continue;
            }
            SpellMechanicsSourceOwner source = sourceOwners.get(id);
            validateSourceRevision(source);
            byte[] bytes = sourceBytesByResource.computeIfAbsent(source.revision().resourcePath(), resourcePath -> {
                try {
                    return resources.getResource("classpath:" + resourcePath).getContentAsByteArray();
                } catch (IOException exception) {
                    throw new IllegalStateException("Cannot load pinned Spell evidence resource", exception);
                }
            });
            var evidence = new SpellMechanicsEvidenceSource(definition, source, bytes, mapper);
            MechanicsDocument extracted = pattern.extract(definition, evidence, document);
            List<MechanicsSourceContent> sourceContent = extracted.sources().stream()
                    .map(revision -> new MechanicsSourceContent(revision, sourceBytesByResource.computeIfAbsent(
                            revision.resourcePath(), resourcePath -> {
                                try {
                                    return resources.getResource("classpath:" + resourcePath).getContentAsByteArray();
                                } catch (IOException exception) {
                                    throw new IllegalStateException("Cannot load pinned evidence resource", exception);
                                }
                            })))
                    .toList();
            MechanicsSourceEvidenceVerifier.verify(extracted, sourceContent,
                    trustedRevisions(extracted, manifest), trustedOwnerPointers(extracted, manifest), mapper);
            ModelChecks.require(extracted.owner().equals(document.owner())
                            && extracted.processing().state() == ProcessingState.PROCESSED,
                    "Supported Spell pattern did not produce a processed owner document");
            ModelChecks.require(extractedSupportedOwners.add(id), "Duplicate extracted supported owner");
            result.add(extracted);
        }
        ModelChecks.require(seen.equals(owners.keySet()), "Missing extraction documents");
        ModelChecks.require(extractedSupportedOwners.equals(supportedPatterns.keySet()),
                "Missing extraction results for supported Spell owners");
        result.sort(Comparator.comparing(document -> document.owner().id()));
        return List.copyOf(result);
    }

    private static void validateSourceRevision(SpellMechanicsSourceOwner owner) {
        ShieldMechanicsEdition shieldEdition = ShieldMechanicsEdition.forId(owner.id());
        if (shieldEdition != null) {
            shieldEdition.validatePinned(owner);
            return;
        }

        SpellMechanicsSourceEdition sourceEdition = SpellMechanicsSourceEdition.forCode(
                owner.source().identity().code());
        ModelChecks.require(sourceEdition != null,
                "Spell extraction pattern selected for an unsupported source edition");
        sourceEdition.validateSourceRevision(owner);
    }

    private static List<com.thedndpub.data.mechanics.model.SourceArtifactRevision> trustedRevisions(
            MechanicsDocument document, List<SpellMechanicsSourceOwner> manifest) {
        var required = new HashSet<>(document.sources());
        var trusted = new HashSet<com.thedndpub.data.mechanics.model.SourceArtifactRevision>();
        for (SpellMechanicsSourceOwner sourceOwner : manifest) {
            if (required.contains(sourceOwner.revision())) {
                validateSourceRevision(sourceOwner);
                trusted.add(sourceOwner.revision());
            }
        }
        ModelChecks.require(trusted.equals(required),
                "Every evidence source revision must be present and validated in the pinned manifest");
        return trusted.stream().sorted(Comparator.comparing(
                com.thedndpub.data.mechanics.model.SourceArtifactRevision::resourcePath)).toList();
    }

    private static List<MechanicsSourceOwnerPointer> trustedOwnerPointers(
            MechanicsDocument document, List<SpellMechanicsSourceOwner> manifest) {
        return manifest.stream()
                .filter(sourceOwner -> document.sources().contains(sourceOwner.revision()))
                .map(sourceOwner -> new MechanicsSourceOwnerPointer(
                        new MechanicsOwnerReference(CatalogDomain.SPELL, sourceOwner.id().value(), sourceOwner.source()),
                        sourceOwner.revision(), sourceOwner.jsonPointer()))
                .toList();
    }
}
