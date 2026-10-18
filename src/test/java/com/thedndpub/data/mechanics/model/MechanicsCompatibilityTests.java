package com.thedndpub.data.mechanics.model;

import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import com.thedndpub.data.services.SpellPublicationCatalog;
import com.thedndpub.data.services.SpellsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thedndpub.data.mechanics.model.MechanicsModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MechanicsCompatibilityTests {
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private SpellFormulaAnnotationCatalog formulas;
    @Autowired
    private SourceRegistry sources;
    @Autowired
    private SpellsService service;
    @Autowired
    private SpellPublicationCatalog publication;

    @Test
    void passiveModelsDoNotChangeExistingSpellsStatusOrPublicationAuthorization() {
        assertEquals(969, spells.size());
        assertEquals(930, formulas.index().annotations().size());
        assertEquals(250, publication.index().candidates().size());
        assertTrue(publication.index().authorizedObservations().isEmpty());
        Map<String, Integer> distribution = new HashMap<>();
        for (var spell : spells.findAll()) {
            distribution.merge(service.findById(spell.id().value()).formulaProjectionStatus().name(), 1, Integer::sum);
        }
        assertEquals(Map.of("NONE_OBSERVED", 547, "WITHHELD", 422), distribution);
        MechanicsCatalogValidation.validateSpell(document(List.of(ac())), spells.findAll(), formulas.index(), sources);
    }

    @Test
    void formulaReferencesJoinTheExistingIndexWithExactOwnerAndSpan() {
        var owner = owner("PHB", "fireball", 241);
        var body = body(owner, 139);
        var annotation = formulas.index().bySpellId().get(new SpellId(owner.id())).getFirst();
        var formula = new FormulaObservationReference("fireball-formula-0", annotation.spellId(), 0, artifact("PHB"),
                annotation.observation().tag().evidence());
        var doc = document(owner, List.of(), List.of(body), List.of(formula), List.of());
        MechanicsCatalogValidation.validateSpell(doc, spells.findAll(), formulas.index(), sources);
        var missing = new FormulaObservationReference("missing", formula.spellId(), 999, artifact("PHB"), formula.evidence());
        var missingDoc = document(owner, List.of(), List.of(body), List.of(missing), List.of());
        assertThrows(IllegalArgumentException.class,
                () -> MechanicsCatalogValidation.validateSpell(missingDoc, spells.findAll(), formulas.index(), sources));
        var altered = new FormulaObservationReference("altered", formula.spellId(), 0, artifact("PHB"),
                new SyntaxEvidence(formula.evidence().location(), new SyntaxSpan(0, 1)));
        var alteredDoc = document(owner, List.of(), List.of(body), List.of(altered), List.of());
        assertThrows(IllegalArgumentException.class,
                () -> MechanicsCatalogValidation.validateSpell(alteredDoc, spells.findAll(), formulas.index(), sources));
    }

    @Test
    void declaredDurationAndCanonicalSourceRegistryJoinsRejectMismatches() {
        var declared = proposal(PHB, "duration", new EffectDurationMechanic(
                new DeclaredSpellDurationReference(new SpellId(PHB.id()), 0, artifact("PHB"))),
                ac().context(), complete(), shieldBody());
        MechanicsCatalogValidation.validateSpell(document(List.of(declared)), spells.findAll(), formulas.index(), sources);
        var bad = proposal(PHB, "duration", new EffectDurationMechanic(
                new DeclaredSpellDurationReference(new SpellId(PHB.id()), 999, artifact("PHB"))),
                ac().context(), complete(), shieldBody());
        assertThrows(IllegalArgumentException.class,
                () -> MechanicsCatalogValidation.validateSpell(document(List.of(bad)), spells.findAll(), formulas.index(), sources));
    }
}
