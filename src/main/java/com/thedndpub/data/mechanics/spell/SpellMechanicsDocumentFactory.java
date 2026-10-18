package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.spell.SpellFileDte;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.importer.spell.SpellIdentityFactory;
import com.thedndpub.data.mechanics.model.CatalogDomain;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.FormulaObservationReference;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsObservationReference;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Startup-only typed source manifest. No formula parser or mechanics interpreter is involved. */
public final class SpellMechanicsDocumentFactory {
    private final ObjectMapper mapper;
    private final ResourcePatternResolver resources;

    public SpellMechanicsDocumentFactory(ObjectMapper mapper) {
        this(mapper, new PathMatchingResourcePatternResolver());
    }

    public SpellMechanicsDocumentFactory(ObjectMapper mapper, ResourcePatternResolver resources) {
        this.mapper = ModelChecks.required(mapper, "mapper");
        this.resources = ModelChecks.required(resources, "resources");
    }

    public List<MechanicsDocument> unattempted(
            List<SpellDefinition> definitions, SpellFormulaAnnotationIndex formulaIndex) {
        return unattempted(definitions, formulaIndex, loadOwners());
    }

    public List<MechanicsDocument> unattempted(
            List<SpellDefinition> definitions,
            SpellFormulaAnnotationIndex formulaIndex,
            List<SpellMechanicsSourceOwner> owners) {
        Map<SpellId, SpellDefinition> expected = new TreeMap<>(Comparator.comparing(SpellId::value));
        for (SpellDefinition definition : definitions) {
            ModelChecks.require(expected.putIfAbsent(definition.id(), definition) == null, "Duplicate Spell definition");
        }
        ModelChecks.require(expected.keySet().equals(formulaIndex.bySpellId().keySet()), "Formula owner set mismatch");
        var seen = new HashSet<SpellId>();
        List<MechanicsDocument> documents = new ArrayList<>();
        for (SpellMechanicsSourceOwner owner : owners) {
            SpellDefinition definition = expected.get(owner.id());
            ModelChecks.require(definition != null && seen.add(owner.id()), "Unknown or duplicate authored Spell owner");
            ModelChecks.require(definition.source().equals(owner.source()), "Authored Spell source mismatch");
            List<MechanicsObservationReference> observations = new ArrayList<>();
            for (var annotation : formulaIndex.bySpellId().get(owner.id())) {
                var evidence = annotation.observation().tag().evidence();
                ModelChecks.require(evidence.location().owner().jsonPointer().equals(owner.jsonPointer()),
                        "Formula belongs to a different authored root");
                observations.add(new FormulaObservationReference(
                        owner.id().value() + ":formula:" + annotation.occurrenceIndex(), owner.id(),
                        annotation.occurrenceIndex(), owner.revision(), evidence));
            }
            documents.add(MechanicsDocument.create(
                    new MechanicsOwnerReference(CatalogDomain.SPELL, owner.id().value(), owner.source()), 1,
                    List.of(owner.revision()), new MechanicsProcessing(ProcessingState.NOT_ATTEMPTED, null, null),
                    List.of(), observations, List.of(), List.of(),
                    new MechanicsCoverage(CoverageStatus.NOT_ASSESSED, null, null, List.of(), List.of(), 0)));
        }
        ModelChecks.require(seen.equals(expected.keySet()), "Missing authored Spell owners");
        documents.sort(Comparator.comparing(document -> document.owner().id()));
        return List.copyOf(documents);
    }

    public List<SpellMechanicsSourceOwner> loadOwners() {
        try {
            Resource[] files = resources.getResources("classpath*:data/spells/*.json");
            ModelChecks.require(files.length > 0, "Missing Spell source resources");
            Arrays.sort(files, Comparator.comparing(Resource::getFilename));
            var paths = new HashSet<String>();
            List<SpellMechanicsSourceOwner> owners = new ArrayList<>();
            for (Resource file : files) {
                String filename = ModelChecks.text(file.getFilename(), "Spell resource filename");
                String path = "data/spells/" + filename;
                ModelChecks.require(paths.add(path), "Duplicate Spell source resource");
                byte[] bytes = file.getContentAsByteArray();
                SpellFileDte document = mapper.readerFor(SpellFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(bytes);
                var revision = new SourceArtifactRevision("5eTools v2.37.0", path, MechanicsHashes.sha256(bytes));
                for (int index = 0; index < document.spells().size(); index++) {
                    var spell = document.spells().get(index);
                    owners.add(new SpellMechanicsSourceOwner(SpellIdentityFactory.id(spell.name(), spell.source()),
                            new SourceReference(spell.source(), spell.page()), revision, "/spell/" + index));
                }
            }
            return List.copyOf(owners);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot load pinned Spell inspection source manifest", exception);
        }
    }
}
