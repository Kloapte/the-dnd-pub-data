package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenericMechanicsRecognitionPipelineTests {
    private final MechanicStrategyRegistry strategies = MechanicStrategyRegistry.standard();

    @Test
    void oneAuthoredFragmentCanProduceSeveralIndependentMechanicCandidates() {
        var fragment = new SourceFragment("spell:test#entries/0", "/entries/0",
                SourceFragment.Kind.AUTHORED_TEXT,
                "The target makes a Constitution saving throw or takes {@damage 2d6} fire damage.",
                List.of("entries", "Effect"), List.of(MechanicType.DAMAGE, MechanicType.SAVING_THROW));
        var candidates = new MechanicTypeDeterminer().determine(List.of(fragment));
        assertEquals(List.of(MechanicType.DAMAGE, MechanicType.SAVING_THROW),
                candidates.stream().map(MechanicCandidate::type).toList());
        assertTrue(candidates.stream().allMatch(candidate -> candidate.fragmentId().equals(fragment.id())));
    }

    @Test
    void damageStrategyUsesAnExplicitAdjacentDamageType() {
        var fragment = new SourceFragment("spell:test#entries/0", "/entries/0",
                SourceFragment.Kind.AUTHORED_TEXT, "takes {@damage 2d6} fire damage.", List.of("entries"),
                List.of(MechanicType.DAMAGE));
        var candidate = candidate(MechanicType.DAMAGE, fragment);
        var result = strategies.strategy(MechanicType.DAMAGE).extract(candidate, fragment, null);
        assertTrue(result.supported());
        assertInstanceOf(DamageMechanic.class, result.payload());
        var damage = (DamageMechanic) result.payload();
        assertEquals(2, ((com.thedndpub.data.mechanics.model.DiceAmount) damage.amount()).count());
        assertEquals("fire", ((com.thedndpub.data.mechanics.model.FixedDamageType) damage.damageType()).authoredCode());
    }

    @Test
    void damageStrategyExtractsMultipleTypedComponentsFromOneAuthoredFragment() {
        var fragment = new SourceFragment("spell:test#entries/0", "/entries/0",
                SourceFragment.Kind.AUTHORED_TEXT,
                "Each creature takes {@damage 2d6} fire damage and {@damage 1d6} cold damage.",
                List.of("entries"), List.of(MechanicType.DAMAGE));
        var result = strategies.strategy(MechanicType.DAMAGE)
                .extract(candidate(MechanicType.DAMAGE, fragment), fragment, null);

        assertEquals(2, result.payloads().size());
        assertEquals(List.of("fire", "cold"), result.payloads().stream()
                .map(item -> ((com.thedndpub.data.mechanics.model.FixedDamageType)
                        ((DamageMechanic) item.payload()).damageType()).authoredCode()).toList());
    }

    @Test
    void saveStrategyRequiresAnUnambiguousAbilityAndLeavesDcUnspecified() {
        var fragment = new SourceFragment("spell:test#entries/0", "/entries/0",
                SourceFragment.Kind.AUTHORED_TEXT, "Make a Dexterity saving throw.", List.of("entries"),
                List.of(MechanicType.SAVING_THROW));
        var result = strategies.strategy(MechanicType.SAVING_THROW)
                .extract(candidate(MechanicType.SAVING_THROW, fragment), fragment, null);
        assertTrue(result.supported());
        assertInstanceOf(SavingThrowRequirementMechanic.class, result.payload());
        var ambiguous = new SourceFragment("spell:test#entries/1", "/entries/1",
                SourceFragment.Kind.AUTHORED_TEXT,
                "Make a Dexterity or Wisdom saving throw.", List.of("entries"), List.of(MechanicType.SAVING_THROW));
        assertFalse(strategies.strategy(MechanicType.SAVING_THROW)
                .extract(candidate(MechanicType.SAVING_THROW, ambiguous), ambiguous, null).supported());
    }

    @Test
    void classificationRetainsUnsupportedCandidateFamiliesInsteadOfDroppingThem() {
        var fragment = new SourceFragment("spell:test#savingThrow/0", "/savingThrow/0",
                SourceFragment.Kind.STRUCTURED_FIELD, "constitution", List.of("savingThrow"),
                List.of(MechanicType.SAVING_THROW));
        var candidate = new MechanicTypeDeterminer().determine(List.of(fragment)).getFirst();
        assertTrue(candidate.ambiguous());
        assertFalse(strategies.strategy(candidate.type()).extract(candidate, fragment, null).supported());
    }

    @Test
    void registryAllowsMultipleStrategiesForOneFamilyAndOrdersThemDeterministically() {
        MechanicExtractionStrategy second = strategy("second");
        MechanicExtractionStrategy first = strategy("first");
        var registry = new MechanicStrategyRegistry(List.of(second, first));
        assertEquals(List.of("first", "second"), registry.strategies(MechanicType.DAMAGE).stream()
                .map(MechanicExtractionStrategy::strategyId).toList());
    }

    private static MechanicCandidate candidate(MechanicType type, SourceFragment fragment) {
        return new MechanicCandidate(type, fragment.id(), "test", List.of(fragment.pointer()),
                String.join(" > ", fragment.hierarchy()), false);
    }

    private static MechanicExtractionStrategy strategy(String id) {
        return new MechanicExtractionStrategy() {
            @Override public String strategyId() { return id; }
            @Override public String version() { return "1"; }
            @Override public java.util.Set<MechanicType> supportedTypes() { return java.util.Set.of(MechanicType.DAMAGE); }
            @Override public StrategyExtraction extract(MechanicCandidate candidate, SourceFragment fragment,
                    com.thedndpub.data.dto.spell.SpellDefinition spell) {
                return StrategyExtraction.unsupported("TEST", "test strategy");
            }
        };
    }
}
