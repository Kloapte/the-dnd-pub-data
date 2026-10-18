package com.thedndpub.data.mechanics.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AnyTrigger;
import com.thedndpub.data.mechanics.model.ArmorClassModifierMechanic;
import com.thedndpub.data.mechanics.model.AttackEventKind;
import com.thedndpub.data.mechanics.model.CasterSubject;
import com.thedndpub.data.mechanics.model.CatalogDomain;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.FormulaObservationReference;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HealingMode;
import com.thedndpub.data.mechanics.model.HitByAttackTrigger;
import com.thedndpub.data.mechanics.model.IntegerModifier;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.MechanicsOrigin;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.MechanicsReferenceDisposition;
import com.thedndpub.data.mechanics.model.ReactionActionCost;
import com.thedndpub.data.mechanics.model.ReactionActivationMechanic;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.SpellDamagePreventionMechanic;
import com.thedndpub.data.mechanics.model.SpellMentionForm;
import com.thedndpub.data.mechanics.model.SpellMentionReference;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.TargetedBySpellTrigger;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TurnActor;
import com.thedndpub.data.mechanics.model.TurnBoundary;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.model.UntilTurnBoundary;
import com.thedndpub.data.mechanics.syntax.SyntaxContext;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Manually proposed assertions, not an extractor or independent gold truth.
 * Explicit offline main regenerates pending fixtures; ordinary tests never write fixtures.
 */
public final class BenchmarkFixtureAuthoring {
    private final MechanicsOwnerReference owner;
    private final SourceArtifactRevision source;
    private final String root;
    private final JsonNode tree;
    private final List<ExpectedMechanic> claims = new ArrayList<>();
    private final List<ExpectedFormula> formulas = new ArrayList<>();
    private final List<DeferredExpectation> deferred = new ArrayList<>();

    private BenchmarkFixtureAuthoring(String code, String slug, int page, int rootIndex) throws IOException {
        owner = new MechanicsOwnerReference(CatalogDomain.SPELL,
                "spell:" + code.toLowerCase(Locale.ROOT) + ":" + slug, new SourceReference(code, page));
        String path = "data/spells/spells-" + code.toLowerCase(Locale.ROOT) + ".json";
        byte[] bytes = new ClassPathResource(path).getContentAsByteArray();
        source = new SourceArtifactRevision("5eTools v2.37.0", path, MechanicsHashes.sha256(bytes));
        root = "/spell/" + rootIndex;
        tree = MechanicsJson.mapper().readTree(bytes);
        if (!tree.at(root + "/source").asText().equals(code)) {
            throw new IllegalArgumentException("Manual fixture pointer/source mismatch");
        }
    }

    public static List<SpellBenchmarkFixture> proposed() throws IOException {
        return List.of(shield(false), shield(true), fireball(), healingWord(), magicMissile(), prestidigitation());
    }

    public static void main(String[] arguments) throws IOException {
        if (arguments.length != 1 || !arguments[0].equals("--write-pending-fixtures")) {
            throw new IllegalArgumentException("Explicit --write-pending-fixtures is required");
        }
        Path directory = Path.of("src/test/resources/mechanics/benchmarks/spells/v1");
        Files.createDirectories(directory);
        for (SpellBenchmarkFixture fixture : proposed()) {
            String name = fixture.owner().id().replace(':', '-') + ".json";
            Path destination = directory.resolve(name);
            if (Files.exists(destination)) {
                var existing = MechanicsJson.mapper().readTree(Files.readAllBytes(destination));
                if (!existing.at("/review/state").asText().equals("PENDING_HUMAN_REVIEW")
                        || !existing.at("/review/humanDecision").isNull()) {
                    throw new IllegalStateException("Never overwrite human-adjudicated fixtures with proposed assertions");
                }
            }
            String json = MechanicsJson.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(fixture) + "\n";
            Files.writeString(destination, json);
        }
    }

    private static SpellBenchmarkFixture shield(boolean revised) throws IOException {
        var author = new BenchmarkFixtureAuthoring(revised ? "XPHB" : "PHB", "shield",
                revised ? 316 : 275, revised ? 304 : 294);
        String body = "/entries/0";
        String condition = "/time/0/condition";
        var caster = new CasterSubject();
        var turnBoundary = new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN);
        SpellMentionReference triggerMention = new SpellMentionReference(revised ? "Magic Missile" : "magic missile",
                null, revised ? SpellMentionForm.PROSE_MENTION : SpellMentionForm.TAG_REFERENCE,
                MechanicsReferenceDisposition.NOT_ATTEMPTED, null, null);
        var reaction = new ReactionActivationMechanic(new ReactionActionCost(1), new AnyTrigger(List.of(
                new HitByAttackTrigger(revised ? AttackEventKind.ATTACK_ROLL_HIT : AttackEventKind.ATTACK_HIT),
                new TargetedBySpellTrigger(triggerMention))));
        author.claim("activation", MechanicFamily.REACTION_ACTIVATION, "shield-reaction-v1", reaction,
                new ExpectedContext(caster, List.of()), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM, List.of(
                        author.field("/time/0/number", "/payload/cost/amount", EvidenceRole.VALUE),
                        author.field("/time/0/unit", "/payload", EvidenceRole.EFFECT_ROLE),
                        author.text(condition, null, "/payload/trigger", EvidenceRole.QUALIFIER),
                        author.text(condition, "you are hit", "/context/subject", EvidenceRole.QUALIFIER)));
        author.claim("ac-bonus", MechanicFamily.ARMOR_CLASS_MODIFIER, "shield-ac-v1",
                new ArmorClassModifierMechanic(new IntegerModifier(5, "+5")),
                new ExpectedContext(caster, List.of(new ExpectedActivation("activation"),
                        new ExpectedDuration(turnBoundary), new ExpectedTriggeringAttack())),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM, List.of(
                        author.text(body, "+5 bonus to AC", "/payload", EvidenceRole.EFFECT_ROLE),
                        author.text(body, "you have", "/context/subject", EvidenceRole.QUALIFIER),
                        author.text(condition, null, "/context/bindings/0", EvidenceRole.QUALIFIER),
                        author.text(body, "Until the start of your next turn", "/context/bindings/1", EvidenceRole.QUALIFIER),
                        author.text(body, "including against the triggering attack", "/context/bindings/2", EvidenceRole.QUALIFIER)));
        author.claim("structured-duration", MechanicFamily.DURATION, "spell-declared-duration-v1",
                new EffectDurationMechanic(new DeclaredSpellDurationReference(new SpellId(author.owner.id()), 0, author.source)),
                new ExpectedContext(caster, List.of()), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM, List.of(
                        author.field("/duration/0", "/payload", EvidenceRole.EFFECT_ROLE),
                        author.field("/range/distance/type", "/context/subject", EvidenceRole.QUALIFIER)));
        author.claim("turn-boundary", MechanicFamily.DURATION, "shield-turn-boundary-v1",
                new EffectDurationMechanic(turnBoundary), new ExpectedContext(caster, List.of()),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM, List.of(
                        author.text(body, "Until the start of your next turn", "/payload", EvidenceRole.EFFECT_ROLE),
                        author.text(body, "your next turn", "/context/subject", EvidenceRole.QUALIFIER)));
        String token = revised ? "{@spell Magic Missile|XPHB}" : "{@spell magic missile}";
        var interaction = new SpellDamagePreventionMechanic(new SpellMentionReference(
                revised ? "Magic Missile" : "magic missile", revised ? new SourceIdentity("XPHB") : null,
                SpellMentionForm.TAG_REFERENCE, MechanicsReferenceDisposition.NOT_ATTEMPTED, null, null));
        author.claim("magic-missile-protection", MechanicFamily.SPELL_DAMAGE_PREVENTION,
                "shield-special-interaction-v1", interaction,
                new ExpectedContext(caster, List.of(new ExpectedActivation("activation"), new ExpectedDuration(turnBoundary))),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM, List.of(
                        author.text(body, "you take no damage from " + token, "/payload", EvidenceRole.EFFECT_ROLE),
                        author.text(body, "you take", "/context/subject", EvidenceRole.QUALIFIER),
                        author.text(condition, null, "/context/bindings/0", EvidenceRole.QUALIFIER),
                        author.text(body, "Until the start of your next turn", "/context/bindings/1", EvidenceRole.QUALIFIER)));
        return author.finish(List.of(
                new BenchmarkProhibition("no-damage-formula", ProhibitionRule.NO_FAMILY, MechanicFamily.DAMAGE,
                        "Shield does not author a damage formula or an applied damage effect."),
                new BenchmarkProhibition("no-invented-save", ProhibitionRule.NO_FAMILY, MechanicFamily.SAVING_THROW_REQUIREMENT,
                        "No saving throw is authored."),
                new BenchmarkProhibition("no-inferred-resolution", ProhibitionRule.NO_RESOLVED_SPELL_REFERENCES, null,
                        "No resolver evidence is established for these proposed mentions; never qualify the XPHB prose trigger.")));
    }

    private static SpellBenchmarkFixture fireball() throws IOException {
        var author = new BenchmarkFixtureAuthoring("PHB", "fireball", 241, 139);
        author.formula(0, "/entries/0", "{@damage 8d6}");
        author.formula(1, "/entriesHigherLevel/0/entries/0", "{@scaledamage 8d6|3-9|1d6}");
        var subject = new UnknownSubject(UnknownValueReason.NOT_INTERPRETED);
        author.claim("saving-throw", MechanicFamily.SAVING_THROW_REQUIREMENT, "fireball-save-v1",
                new SavingThrowRequirementMechanic(new FixedAbility(Ability.DEXTERITY),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)),
                new ExpectedContext(subject, List.of()), MechanicsCompleteness.PARTIAL, List.of(
                        author.text("/entries/0", "must make a Dexterity saving throw", "/payload", EvidenceRole.EFFECT_ROLE),
                        author.field("/savingThrow/0", "/payload/ability", EvidenceRole.VALUE)));
        author.claim("failed-save-damage", MechanicFamily.DAMAGE, "fireball-damage-v1",
                new DamageMechanic(new DiceAmount(8, 6, 0), new FixedDamageType("fire")),
                new ExpectedContext(subject, List.of(new ExpectedSaveOutcome("saving-throw", SaveOutcome.FAILURE))),
                MechanicsCompleteness.PARTIAL, List.of(
                        author.text("/entries/0", "A target takes {@damage 8d6} fire damage on a failed save",
                                "/payload", EvidenceRole.EFFECT_ROLE),
                        author.text("/entries/0", "on a failed save", "/context/bindings/0", EvidenceRole.QUALIFIER)));
        author.defer("successful-save-half", MechanicFamily.DAMAGE, "SAVE_SUCCESS_OUTCOME",
                "Successful save halves the relevant damage; do not evaluate a rounded amount.",
                "The canonical amount vocabulary has no outcome-relative half expression.", "/entries/0",
                "half as much damage on a successful one");
        author.defer("area-targets", MechanicFamily.UNDETERMINED, "AREA_AND_TARGETING",
                "Chosen point within range; creatures in a 20-foot-radius sphere; preserve worn/carried object exceptions.",
                "Target, area and object ignition families are not implemented.", "/entries/0", "20-foot-radius sphere");
        author.defer("slot-scaling", MechanicFamily.DAMAGE, "SLOT_SCALING",
                "Slot level 4 or higher adds 1d6 per slot level above 3; do not infer executable arithmetic.",
                "Scaling syntax is not itself an applied scaling relationship.", "/entriesHigherLevel/0/entries/0", null);
        return author.finish(List.of());
    }

    private static SpellBenchmarkFixture healingWord() throws IOException {
        var author = new BenchmarkFixtureAuthoring("PHB", "healing-word", 250, 175);
        author.formula(0, "/entries/0", "{@dice 1d4}");
        author.formula(1, "/entriesHigherLevel/0/entries/0", "{@scaledice 1d4|1-9|1d4}");
        author.claim("healing", MechanicFamily.HEALING, "healing-word-amount-v1",
                new HealingMechanic(new SumAmount(List.of(new DiceAmount(1, 4, 0),
                        new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER))), HealingMode.RESTORE_HIT_POINTS),
                new ExpectedContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()),
                MechanicsCompleteness.PARTIAL, List.of(author.text("/entries/0",
                        "regains hit points equal to {@dice 1d4} + your spellcasting ability modifier",
                        "/payload", EvidenceRole.EFFECT_ROLE)));
        author.defer("target-restrictions", MechanicFamily.HEALING, "TARGET_RESTRICTIONS",
                "One chosen visible creature in range; no effect on undead or constructs.",
                "Target selection and exclusions lack a complete canonical representation.", "/entries/0", null);
        author.defer("slot-scaling", MechanicFamily.HEALING, "SLOT_SCALING",
                "Add 1d4 per slot level above 1 when using slot level 2 or higher.",
                "Do not truncate the base amount to a dice token or evaluate the symbolic modifier.",
                "/entriesHigherLevel/0/entries/0", null);
        return author.finish(List.of());
    }

    private static SpellBenchmarkFixture magicMissile() throws IOException {
        var author = new BenchmarkFixtureAuthoring("PHB", "magic-missile", 257, 213);
        author.formula(0, "/entries/0", "{@damage 1d4 + 1}");
        author.defer("per-projectile", MechanicFamily.DAMAGE, "PROJECTILE_DAMAGE",
                "1d4 + 1 force damage is per dart, not the entire spell; three darts initially.",
                "The canonical model cannot preserve per-projectile ownership; no proposed DamageMechanic is emitted.",
                "/entries/0", "A dart deals {@damage 1d4 + 1} force damage to its target");
        author.defer("distribution", MechanicFamily.UNDETERMINED, "PROJECTILE_DISTRIBUTION",
                "Darts strike simultaneously and can be distributed to one or several visible creatures in range.",
                "No projectile allocation, selected targets, total damage or attack execution is inferred.",
                "/entries/0", "The darts all strike simultaneously");
        author.defer("slot-scaling", MechanicFamily.UNDETERMINED, "PROJECTILE_SCALING",
                "One additional dart per slot level above 1 when using slot level 2 or higher.",
                "Projectile scaling is not a formula observation or supported payload.",
                "/entriesHigherLevel/0/entries/0", null);
        return author.finish(List.of());
    }

    private static SpellBenchmarkFixture prestidigitation() throws IOException {
        var author = new BenchmarkFixtureAuthoring("PHB", "prestidigitation", 267, 260);
        List<String> concepts = List.of("harmless sensory effect", "light or snuff small flame",
                "clean or soil a bounded object", "temperature or flavor of bounded material",
                "color or mark or symbol", "hand-sized trinket or image with turn boundary");
        for (int index = 0; index < concepts.size(); index++) {
            author.defer("choice-" + index, MechanicFamily.UNDETERMINED, "EFFECT_ALTERNATIVE",
                    "Alternative " + (index + 1) + ": " + concepts.get(index) + "; preserve the authored list grouping.",
                    "No effect-choice payload or player selection is implemented.", "/entries/1/items/" + index, null);
        }
        author.defer("concurrent-effects", MechanicFamily.UNDETERMINED, "CONCURRENT_LIMIT",
                "At most three non-instantaneous effects from repeated castings may coexist.",
                "Do not apply this limit to instantaneous effects or calculate campaign state.",
                "/entries/2", "up to three of its non-instantaneous effects active at a time");
        author.defer("dismissal", MechanicFamily.UNDETERMINED, "DISMISSAL_ACTION",
                "An active non-instantaneous effect can be dismissed as an action.",
                "Dismissal action family is not implemented; no selected effect is inferred.",
                "/entries/2", "dismiss such an effect as an action");
        return author.finish(List.of());
    }

    private void claim(String key, MechanicFamily family, String pattern, MechanicPayload payload,
                       ExpectedContext context, MechanicsCompleteness completeness, List<ExpectedEvidence> evidence) {
        claims.add(new ExpectedMechanic(key, family, pattern, payload, context, completeness, evidence));
    }

    private void defer(String id, MechanicFamily family, String concept, String constraint, String question,
                       String pointer, String phrase) {
        deferred.add(new DeferredExpectation(id, family, concept, constraint, question,
                UnsupportedReason.FAMILY_NOT_IMPLEMENTED,
                List.of(text(pointer, phrase, "/payload", EvidenceRole.EFFECT_ROLE).evidence())));
    }

    private void formula(int occurrence, String pointer, String token) {
        var text = (TextMechanicsEvidence) text(pointer, token, "/payload", EvidenceRole.VALUE).evidence();
        formulas.add(new ExpectedFormula(new FormulaObservationReference(owner.id() + ":formula:" + occurrence,
                new SpellId(owner.id()), occurrence, source, text.syntax()), token));
    }

    private ExpectedEvidence text(String pointer, String phrase, String claimPath, EvidenceRole role) {
        String fullPointer = root + pointer;
        String text = tree.at(fullPointer).textValue();
        if (text == null) {
            throw new IllegalArgumentException("Not source text: " + fullPointer);
        }
        int start = phrase == null ? 0 : text.indexOf(phrase);
        if (start < 0 || (phrase != null && text.indexOf(phrase, start + 1) >= 0)) {
            throw new IllegalArgumentException("Manual span phrase must occur exactly once: " + fullPointer);
        }
        int end = phrase == null ? text.length() : start + phrase.length();
        String id = "text-" + MechanicsHashes.sha256(fullPointer + ":" + start + ":" + end).substring(0, 16);
        var location = new SyntaxTextLocation(sourceOwner(), fullPointer, hierarchy(fullPointer), SyntaxOrigin.AUTHORED);
        var evidence = new TextMechanicsEvidence(id, owner.source(),
                new SyntaxEvidence(location, new SyntaxSpan(start, end)), MechanicsHashes.sha256(text),
                MechanicsOrigin.AUTHORED, owner);
        return new ExpectedEvidence(evidence, claimPath, role);
    }

    private ExpectedEvidence field(String pointer, String claimPath, EvidenceRole role) {
        String fullPointer = root + pointer;
        String id = "field-" + MechanicsHashes.sha256(fullPointer).substring(0, 16);
        var evidence = new StructuredFieldMechanicsEvidence(id, owner.source(), sourceOwner(), fullPointer,
                hierarchy(fullPointer), MechanicsHashes.sha256(tree.at(fullPointer).toString()),
                MechanicsOrigin.AUTHORED, owner);
        return new ExpectedEvidence(evidence, claimPath, role);
    }

    private SyntaxSourceOwner sourceOwner() {
        return new SyntaxSourceOwner(source.resourcePath(), source.resourceSha256(), "spell", root);
    }

    private List<SyntaxContext> hierarchy(String pointer) {
        List<SyntaxContext> result = new ArrayList<>();
        String current = root;
        while (pointer.startsWith(current + "/")) {
            JsonNode node = tree.at(current);
            result.add(new SyntaxContext(current, node.isObject() ? node.path("type").textValue() : null));
            int next = pointer.indexOf('/', current.length() + 1);
            if (next < 0) {
                break;
            }
            current = pointer.substring(0, next);
        }
        return List.copyOf(result);
    }

    private SpellBenchmarkFixture finish(List<BenchmarkProhibition> additional) {
        List<BenchmarkProhibition> prohibitions = new ArrayList<>(additional);
        prohibitions.add(new BenchmarkProhibition("no-cross-edition-evidence", ProhibitionRule.NO_FOREIGN_SOURCE_EVIDENCE,
                null, "Never reuse PHB and XPHB evidence or original source locations interchangeably."));
        prohibitions.add(new BenchmarkProhibition("no-invented-mechanics", ProhibitionRule.NO_UNEXPECTED_CLAIMS, null,
                "No calculated final AC, guaranteed attack negation, broad immunity, invented targets/triggers or effects; "
                        + "only the exact bounded proposed claims may be compared as supported assertions."));
        String id = "spell-benchmark:" + owner.id().substring("spell:".length());
        var content = new FixtureContent("1", id, owner, source, root, List.copyOf(claims), List.copyOf(formulas),
                List.copyOf(deferred), List.copyOf(prohibitions));
        return new SpellBenchmarkFixture("1", id, new FixtureRevision(1, BenchmarkFixtures.fingerprint(content)),
                owner, source, root, claims, formulas, deferred, prohibitions,
                new FixtureReview(FixtureLifecycle.PENDING_HUMAN_REVIEW, null));
    }
}
