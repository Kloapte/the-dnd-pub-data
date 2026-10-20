package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageOutcome;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HealingMode;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicContextBinding;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.ScalingEffect;
import com.thedndpub.data.mechanics.model.SpellSlotScalingMechanic;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.MarkupSyntaxException;
import com.thedndpub.data.mechanics.syntax.MarkupTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Explicit PHB pilot patterns, not a general language parser or an executable spell definition. */
public final class PhbSpellMechanicsExtractor implements SpellMechanicsPattern {
    private static final Set<String> DAMAGE_OWNERS = Set.of(
            "spell:phb:fireball",
            "spell:phb:burning-hands",
            "spell:phb:cone-of-cold",
            "spell:phb:shatter",
            "spell:phb:lightning-bolt",
            "spell:phb:circle-of-death",
            "spell:phb:blight",
            "spell:phb:chain-lightning",
            "spell:phb:cloudkill",
            "spell:phb:fire-storm",
            "spell:phb:hellish-rebuke",
            "spell:phb:incendiary-cloud",
            "spell:phb:insect-plague",
            "spell:phb:moonbeam",
            "spell:phb:flaming-sphere",
            "spell:phb:wind-wall");
    private static final Set<String> HEALING_OWNERS = Set.of(
            "spell:phb:healing-word",
            "spell:phb:cure-wounds",
            "spell:phb:mass-cure-wounds",
            "spell:phb:mass-healing-word",
            "spell:phb:prayer-of-healing");
    private static final Set<String> CONDITION_DEPENDENCY_OWNERS = Set.of(
            "spell:phb:hold-person", "spell:phb:blindness-deafness", "spell:phb:fear",
            "spell:phb:greater-invisibility", "spell:phb:hypnotic-pattern");
    private static final Set<String> MODIFIER_AND_PROTECTION_OWNERS = Set.of(
            "spell:phb:shield-of-faith", "spell:phb:mage-armor", "spell:phb:barkskin",
            "spell:phb:longstrider", "spell:phb:bless", "spell:phb:bane",
            "spell:phb:protection-from-energy", "spell:phb:stoneskin");
    private static final Set<String> PREVIOUSLY_PROCESSED_OWNERS = Set.of(
            "spell:phb:fireball", "spell:phb:healing-word");
    private static final Set<String> GENERALIZED_SAVE_DAMAGE_OWNERS = Set.of(
            "spell:phb:acid-splash",
            "spell:phb:arms-of-hadar",
            "spell:phb:call-lightning",
            "spell:phb:calm-emotions",
            "spell:phb:disintegrate",
            "spell:phb:dissonant-whispers",
            "spell:phb:ice-storm",
            "spell:phb:thunderwave");
    private static final Set<String> GENERALIZED_CONDITION_REMOVAL_OWNERS = Set.of(
            "spell:phb:greater-restoration",
            "spell:phb:lesser-restoration",
            "spell:phb:power-word-heal");
    private static final Set<String> STRUCTURED_CONDITION_IMMUNITY_ASSESSMENT_OWNERS = Set.of(
            "spell:au:iron-body",
            "spell:tce:tasha-s-otherworldly-guise",
            "spell:xphb:freedom-of-movement",
            "spell:xphb:gaseous-form",
            "spell:xphb:heroes-feast",
            "spell:xphb:heroism",
            "spell:xphb:feign-death",
            "spell:xphb:mind-blank");
    private static final Set<String> OWNERS;
    private static final String MAGIC_MISSILE = "spell:phb:magic-missile";
    static final String BODY = "/entries/0";
    private static final String HIGHER = "/entriesHigherLevel/0/entries/0";
    private static final Pattern DICE = Pattern.compile("([1-9][0-9]*)d([1-9][0-9]*)");
    private static final String SAVE = "must make a Dexterity saving throw";
    private static final String FAILURE = "on a failed save";
    private static final String SUCCESS = "half as much damage on a successful one";
    private static final String AREA = "20-foot-radius sphere";
    private static final String FIREBALL_INTRO = "A bright streak flashes from your pointing finger to a point "
            + "you choose within range and then blossoms with a low roar into an explosion of flame. ";
    private static final String HEALING_PREFIX = "A creature of your choice that you can see within range ";
    private static final String HEALING_MODIFIER = " + your spellcasting ability modifier";
    private static final String HEALING_EXCLUSIONS = "This spell has no effect on undead or constructs.";

    private final PhbConditionMechanicsRules conditionRules = new PhbConditionMechanicsRules();
    private final PhbModifierAndProtectionRules modifierRules = new PhbModifierAndProtectionRules();

    static {
        var owners = new java.util.HashSet<>(DAMAGE_OWNERS);
        owners.addAll(HEALING_OWNERS);
        owners.addAll(MODIFIER_AND_PROTECTION_OWNERS);
        OWNERS = Set.copyOf(owners);
    }

    @Override
    public String patternId() {
        return "phb-damage-healing-and-conditions-v1";
    }

    @Override
    public boolean supports(SpellDefinition spell) {
        return usesLegacyImplementation(spell) || GENERALIZED_SAVE_DAMAGE_OWNERS.contains(spell.id().value())
                || GENERALIZED_CONDITION_REMOVAL_OWNERS.contains(spell.id().value())
                || STRUCTURED_CONDITION_IMMUNITY_ASSESSMENT_OWNERS.contains(spell.id().value())
                || PhbMagicMissileRules.OWNER.equals(spell.id().value())
                || PhbSpellAttackRules.PRODUCTION_OWNERS.contains(spell.id().value());
    }

    boolean supportsForInventory(SpellDefinition spell) {
        return supports(spell) || spell.source().identity().code().equalsIgnoreCase("PHB")
                && (!spell.mechanics().savingThrows().isEmpty() || !spell.mechanics().damageInflicted().isEmpty()
                || PhbConditionMechanicsRules.containsConditionTag(spell.entries())
                || PhbConditionMechanicsRules.containsConditionTag(spell.entriesHigherLevel()));
    }

    private boolean usesLegacyImplementation(SpellDefinition spell) {
        return OWNERS.contains(spell.id().value()) || spell.source().identity().code().equalsIgnoreCase("PHB")
                && (!spell.mechanics().conditionsInflicted().isEmpty()
                || !spell.mechanics().conditionImmunities().isEmpty());
    }

    private MechanicsDocument extractGeneralizedSaveAndDamage(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        return PhbSaveDamageGeneralizer.extract(spell, source, original);
    }

    @Override
    public MechanicsDocument extract(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                     MechanicsDocument original) {
        ModelChecks.require(supports(spell), "Unsupported PHB production extraction owner");
        return extractSupported(spell, source, original);
    }

    MechanicsDocument extractForInventory(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        ModelChecks.require(supportsForInventory(spell), "Unsupported PHB inventory extraction owner");
        return extractSupported(spell, source, original);
    }

    private MechanicsDocument extractSupported(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                                MechanicsDocument original) {
        ModelChecks.require(original.owner().equals(source.owner())
                        && original.owner().id().equals(spell.id().value())
                        && original.sources().equals(List.of(source.source().revision())),
                "Spell extraction source/document mismatch");
        ModelChecks.require(original.processing().state() == ProcessingState.NOT_ATTEMPTED,
                "Spell extraction requires an unattempted document");
        verifyEntries(spell.entries(), "/entries", source);
        verifyEntries(spell.entriesHigherLevel(), "/entriesHigherLevel", source);
        source.verifyTextList("/savingThrow", spell.mechanics().savingThrows());
        source.verifyTextList("/damageInflict", spell.mechanics().damageInflicted());
        source.verifyTextList("/conditionInflict", spell.mechanics().conditionsInflicted());
        source.verifyTextList("/conditionImmune", spell.mechanics().conditionImmunities());
        source.verifyTextList("/spellAttack", spell.mechanics().spellAttacks());
        if (spell.id().value().equals(MAGIC_MISSILE)) {
            return PhbMagicMissileRules.extract(spell, source, original);
        }
        if (PhbSpellAttackRules.PRODUCTION_OWNERS.contains(spell.id().value())) {
            return PhbSpellAttackRules.extract(spell, source, original);
        }
        if (STRUCTURED_CONDITION_IMMUNITY_ASSESSMENT_OWNERS.contains(spell.id().value())) {
            return conditionRules.extractStructuredConditionImmunities(spell, source, original);
        }
        if (!usesLegacyImplementation(spell)) {
            if ((GENERALIZED_CONDITION_REMOVAL_OWNERS.contains(spell.id().value())
                    || PhbConditionMechanicsRules.containsConditionTag(spell.entries()))
                    && spell.mechanics().savingThrows().isEmpty()
                    && spell.mechanics().damageInflicted().isEmpty()) {
                return conditionRules.extractConditionRemovals(spell, source, original);
            }
            return extractGeneralizedSaveAndDamage(spell, source, original);
        }
        String adapterId = PREVIOUSLY_PROCESSED_OWNERS.contains(spell.id().value())
                ? "phb-spell-pilot" : "phb-spell-patterns";
        var assembly = new SpellMechanicsAssembly(source, original, adapterId);
        if (spell.id().value().equals("spell:phb:fireball")) {
            MechanicRecord save = assembly.attempt("fireball-save-v1", MechanicFamily.SAVING_THROW_REQUIREMENT,
                    () -> savingThrow(spell, source, assembly));
            assembly.attempt("fireball-damage-v1", MechanicFamily.DAMAGE,
                    () -> damage(spell, source, assembly, save));
            defer(assembly, source, "area-targets", MechanicFamily.UNDETERMINED, BODY, AREA);
            assembly.attempt("slot-scaling-v1", MechanicFamily.SPELL_SLOT_SCALING,
                    () -> slotScaling(spell, source, assembly));
        } else if (spell.id().value().equals("spell:phb:healing-word")) {
            assembly.attempt("healing-word-amount-v1", MechanicFamily.HEALING,
                    () -> healing(spell, source, assembly));
            defer(assembly, source, "target-restrictions", MechanicFamily.HEALING, BODY, null);
            assembly.attempt("slot-scaling-v1", MechanicFamily.SPELL_SLOT_SCALING,
                    () -> slotScaling(spell, source, assembly));
        } else if (DAMAGE_OWNERS.contains(spell.id().value())) {
            extractGeneralDamage(spell, source, assembly);
        } else if (HEALING_OWNERS.contains(spell.id().value())) {
            extractGeneralHealing(spell, source, assembly);
        }
        if (MODIFIER_AND_PROTECTION_OWNERS.contains(spell.id().value())) {
            modifierRules.extractModifierAndProtection(spell, source, assembly);
        }
        conditionRules.extractConditions(spell, source, assembly);
        if (!spell.entriesHigherLevel().isEmpty()
                && !spell.id().value().equals("spell:phb:fireball")
                && !spell.id().value().equals("spell:phb:healing-word")) {
            assembly.attempt("slot-scaling-v1", MechanicFamily.SPELL_SLOT_SCALING,
                    () -> slotScaling(spell, source, assembly));
        }
        String root = source.source().jsonPointer();
        return assembly.finish(List.of(root + "/entries", root + "/entriesHigherLevel",
                        root + "/savingThrow", root + "/damageInflict"),
                List.of(root + "/entries", root + "/entriesHigherLevel"));
    }

    private void extractGeneralDamage(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        String bodyPointer = generalDamagePointer(spell, source);
        MechanicRecord save = assembly.attempt("save-requirement-v1", MechanicFamily.SAVING_THROW_REQUIREMENT,
                () -> generalSavingThrow(spell, source, assembly, bodyPointer));
        MechanicRecord damage = assembly.attempt("damage-on-failed-save-v1", MechanicFamily.DAMAGE,
                () -> generalFailedSaveDamage(spell, source, assembly, save, bodyPointer));
        if (damage != null && damage.context().bindings().stream().noneMatch(binding ->
                binding instanceof AppliesOnSaveOutcome outcome && outcome.outcome() == SaveOutcome.SUCCESS)) {
            assembly.abstain("successful-save-outcome", MechanicFamily.DAMAGE,
                    UnsupportedReason.NO_SUPPORTED_PATTERN, BODY);
        }
    }

    private MechanicRecord generalSavingThrow(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            String bodyPointer) {
        String text = source.text(bodyPointer).text();
        Matcher matcher = Pattern.compile(
                "\\bmust make (?:a )?(Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma) saving throw\\b")
                .matcher(text);
        require(matcher.find(), bodyPointer);
        String authoredAbility = matcher.group(1);
        require(!matcher.find(), bodyPointer);
        List<String> abilities = spell.mechanics().savingThrows();
        require(abilities != null && abilities.size() == 1, "/savingThrow");
        Ability ability = ability(abilities.getFirst());
        require(authoredAbility.equalsIgnoreCase(ability.name()), bodyPointer);
        TextMechanicsEvidence saveText = source.textEvidence(bodyPointer, authoredAbility + " saving throw");
        return assembly.claim("saving-throw", "save-requirement-v1",
                new SavingThrowRequirementMechanic(new FixedAbility(ability),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)), unknownContext(), List.of(
                        MechanicEvidenceAssociation.of(saveText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(source.fieldEvidence("/savingThrow/0"),
                                "/payload/ability", EvidenceRole.VALUE)), false, MechanicsCompleteness.PARTIAL);
    }

    private MechanicRecord generalFailedSaveDamage(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MechanicRecord save,
            String bodyPointer) {
        require(save != null, bodyPointer);
        String text = source.text(bodyPointer).text();
        MarkupTag tag = singleFormula(source, bodyPointer, "damage");
        List<String> types = spell.mechanics().damageInflicted();
        require(types != null && types.size() == 1, "/damageInflict");
        String type = types.getFirst();
        String role = damageRole(text, tag, type);
        require(role != null, bodyPointer);
        requireSaveAndDamageAreLocallyRelated(text, tag, bodyPointer);
        DamageSaveOutcomeEvidence outcomes = saveOutcomeEvidence(save, source, role, bodyPointer);
        TextMechanicsEvidence damageText = source.textEvidence(bodyPointer, role);
        return assembly.claim("failed-save-damage", "damage-on-failed-save-v1",
                new DamageMechanic(dice(tag, BODY), new FixedDamageType(type)),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), outcomes.bindings()),
                combineEvidence(damageText, outcomes.evidence()),
                false, MechanicsCompleteness.PARTIAL);
    }

    private DamageSaveOutcomeEvidence saveOutcomeEvidence(
            MechanicRecord save,
            SpellMechanicsEvidenceSource source,
            String damageRole,
            String bodyPointer) {
        Matcher failedSave = Pattern.compile("(?i)on a failed save").matcher(damageRole);
        require(failedSave.find() && !failedSave.find(), bodyPointer);
        var failedEvidence = source.textEvidence(bodyPointer, "on a failed save");
        var bindings = new java.util.ArrayList<com.thedndpub.data.mechanics.model.MechanicContextBinding>();
        var associations = new java.util.ArrayList<MechanicEvidenceAssociation>();
        bindings.add(new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.FAILURE,
                DamageOutcome.FULL_DAMAGE, List.of(failedEvidence.id())));
        associations.add(MechanicEvidenceAssociation.of(failedEvidence, "/context/bindings/0",
                EvidenceRole.QUALIFIER));

        String sourceText = source.text(bodyPointer).text();
        Matcher success = Pattern.compile("(?i)(?:or\\s+)?(?:half(?: as much)? damage|no damage)"
                        + " on a successful (?:save|one)|on a successful (?:save|one),\\s*"
                        + "(?:a target|the target|a creature|the creature|it) takes "
                        + "(?:half(?: as much)? damage|no damage)")
                .matcher(sourceText);
        if (success.find()) {
            String phrase = success.group();
            Matcher successfulOutcome = Pattern.compile("(?i)successful (?:save|one)").matcher(sourceText);
            require(!success.find() && successfulOutcome.find() && !successfulOutcome.find(), bodyPointer);
            DamageOutcome result = phrase.toLowerCase(java.util.Locale.ROOT).contains("no damage")
                    ? DamageOutcome.NO_DAMAGE : DamageOutcome.HALF_DAMAGE;
            var successEvidence = source.textEvidence(bodyPointer, phrase);
            bindings.add(new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.SUCCESS,
                    result, List.of(successEvidence.id())));
            associations.add(MechanicEvidenceAssociation.of(successEvidence, "/context/bindings/1",
                    EvidenceRole.QUALIFIER));
        }
        return new DamageSaveOutcomeEvidence(List.copyOf(bindings), List.copyOf(associations));
    }

    private List<MechanicEvidenceAssociation> combineEvidence(
            TextMechanicsEvidence damageEvidence,
            List<MechanicEvidenceAssociation> outcomeEvidence) {
        var result = new java.util.ArrayList<MechanicEvidenceAssociation>();
        result.add(MechanicEvidenceAssociation.of(damageEvidence, "/payload", EvidenceRole.EFFECT_ROLE));
        result.addAll(outcomeEvidence);
        return List.copyOf(result);
    }

    private record DamageSaveOutcomeEvidence(
            List<com.thedndpub.data.mechanics.model.MechanicContextBinding> bindings,
            List<MechanicEvidenceAssociation> evidence) {
    }

    private MechanicRecord generalHealing(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        require(spell.entries().size() == 1 && spell.entries().getFirst() instanceof TextEntryDte, "/entries");
        MarkupTag tag = singleFormula(source, "dice");
        String text = source.text(BODY).text();
        String formula = tag.originalText();
        String phrase = "hit points equal to " + formula + HEALING_MODIFIER;
        require(text.contains(phrase) && count(text, phrase) == 1, BODY);
        Matcher leading = Pattern.compile("(?i)\\b(?:regains?|each regain) ").matcher(text);
        require(leading.find(), BODY);
        String role = text.substring(leading.start(), text.indexOf('.', leading.start()) < 0
                ? text.length() : text.indexOf('.', leading.start()));
        require(role.contains(phrase), BODY);
        TextMechanicsEvidence healingText = source.textEvidence(BODY, role);
        return assembly.claim("healing", "healing-amount-v1",
                new HealingMechanic(new SumAmount(List.of(dice(tag, BODY),
                        new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER))),
                        HealingMode.RESTORE_HIT_POINTS), unknownContext(), List.of(
                        MechanicEvidenceAssociation.of(healingText, "/payload", EvidenceRole.EFFECT_ROLE)),
                false, MechanicsCompleteness.PARTIAL);
    }

    private void extractGeneralHealing(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        assembly.attempt("healing-amount-v1", MechanicFamily.HEALING,
                () -> generalHealing(spell, source, assembly));
    }

    private String generalDamagePointer(SpellDefinition spell, SpellMechanicsEvidenceSource source) {
        List<String> candidates = new java.util.ArrayList<>();
        for (int index = 0; index < spell.entries().size(); index++) {
            if (!(spell.entries().get(index) instanceof TextEntryDte)) {
                continue;
            }
            String pointer = "/entries/" + index;
            try {
                MarkupTag tag = singleFormula(source, pointer, "damage");
                String text = source.text(pointer).text();
                if (spell.mechanics().damageInflicted().size() == 1
                        && damageRole(text, tag, spell.mechanics().damageInflicted().getFirst()) != null
                        && Pattern.compile("\\bmust make (?:a )?(?:Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma) saving throw\\b")
                                .matcher(text).results().count() == 1) {
                    candidates.add(pointer);
                }
            } catch (SpellPatternMismatch mismatch) {
                // Other text entries are outside this bounded damage pattern.
            }
        }
        require(candidates.size() == 1, "/entries");
        return candidates.getFirst();
    }

    private MechanicRecord slotScaling(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        require(!spell.entriesHigherLevel().isEmpty(), "/entriesHigherLevel");
        String pointer = HIGHER;
        String text = source.text(pointer).text();
        Matcher matcher = Pattern.compile("(?i)(?:the )?(damage|healing|hit points) increases by \\{@(damage|dice|scaledamage|scaledice) ([1-9][0-9]*d[1-9][0-9]*)(?:\\|[^|}]+\\|([1-9][0-9]*d[1-9][0-9]*))?} for each slot level above ([1-9][0-9]*)(?:st|nd|rd|th)")
                .matcher(text);
        require(matcher.find(), pointer);
        String effectWord = matcher.group(1);
        String tagName = matcher.group(2);
        String formula = matcher.group(4) == null ? matcher.group(3) : matcher.group(4);
        int authoredBase = Integer.parseInt(matcher.group(5));
        require(!matcher.find(), pointer);
        ScalingEffect effect = effectWord.equalsIgnoreCase("damage")
                ? ScalingEffect.DAMAGE : ScalingEffect.HEALING;
        require(spell.level() != null && spell.level() == authoredBase, pointer);
        Matcher applicability = Pattern.compile(
                "(?i)cast this spell using a spell slot of ([1-9][0-9]*)(?:st|nd|rd|th) level or higher")
                .matcher(text);
        require(applicability.find(), pointer);
        int minimumCastingSlotLevel = Integer.parseInt(applicability.group(1));
        require(!applicability.find() && minimumCastingSlotLevel == authoredBase + 1, pointer);
        MarkupTag tag;
        try {
            tag = new BalancedMarkupParser().parse(source.text(pointer)).tags().stream()
                    .filter(value -> value.name().equals(tagName) && !value.arguments().isEmpty()
                            && (value.name().equals("scaledamage") || value.name().equals("scaledice")
                                    ? value.arguments().size() == 3 && value.arguments().get(2).equals(formula)
                                    : value.arguments().getFirst().equals(formula)))
                    .findFirst().orElseThrow(() -> new SpellPatternMismatch(UnsupportedReason.NO_SUPPORTED_PATTERN, pointer));
        } catch (MarkupSyntaxException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, pointer);
        }
        var evidence = source.textEvidence(pointer, text);
        return assembly.claim("slot-scaling", "spell-slot-scaling-v1",
                new SpellSlotScalingMechanic(effect, diceExpression(formula, pointer), authoredBase,
                        minimumCastingSlotLevel, 1), unknownContext(),
                List.of(MechanicEvidenceAssociation.of(evidence, "/payload", EvidenceRole.EFFECT_ROLE)),
                false, MechanicsCompleteness.PARTIAL);
    }

    MechanicRecord generalizedSlotScaling(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        return slotScaling(spell, source, assembly);
    }

    private String damageRole(String text, MarkupTag tag, String damageType) {
        String formula = tag.originalText();
        int index = text.indexOf(formula);
        if (index < 0 || text.indexOf(formula, index + formula.length()) >= 0) {
            return null;
        }
        int start = text.lastIndexOf('.', index);
        start = start < 0 ? 0 : start + 1;
        int end = text.indexOf('.', index + formula.length());
        end = end < 0 ? text.length() : end;
        String clause = text.substring(start, end).trim();
        String escaped = Pattern.quote(formula);
        String type = Pattern.quote(damageType);
        String subject = "(?:a target|the target|a creature|the creature|it)";
        boolean recognized = Pattern.compile("(?is)(?:on a failed save,\\s*)?" + subject
                + " takes\\s+" + escaped + "\\s+" + type
                + " damage(?: on a failed save)?(?:, or (?:half(?: as much)? damage|no damage)"
                + " on a successful (?:save|one))?(?:\\s+(?:and|but)\\b[^.]*)?")
                .matcher(clause).matches();
        if (!recognized || !clause.toLowerCase(java.util.Locale.ROOT).contains("on a failed save")) {
            return null;
        }
        return clause;
    }

    private void requireSaveAndDamageAreLocallyRelated(String text, MarkupTag tag, String bodyPointer) {
        Matcher savingThrow = Pattern.compile(
                "\\bmust make (?:a )?(?:Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma) saving throw\\b")
                .matcher(text);
        require(savingThrow.find(), bodyPointer);
        int saveEnd = savingThrow.end();
        require(!savingThrow.find(), bodyPointer);
        int formulaStart = text.indexOf(tag.originalText());
        require(formulaStart > saveEnd, bodyPointer);
        String between = text.substring(saveEnd, formulaStart);
        require(between.chars().filter(character -> character == '.').count() <= 1, bodyPointer);
    }

    static Ability ability(String authored) {
        try {
            return Ability.valueOf(authored.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, "/savingThrow/0");
        }
    }

    private int count(String value, String substring) {
        int occurrences = 0;
        int searchFrom = 0;
        int matchAt = value.indexOf(substring, searchFrom);
        while (matchAt >= 0) {
            occurrences++;
            searchFrom = matchAt + substring.length();
            matchAt = value.indexOf(substring, searchFrom);
        }
        return occurrences;
    }

    private MechanicRecord savingThrow(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                       SpellMechanicsAssembly assembly) {
        fireballBody(spell, source);
        require(spell.mechanics().savingThrows().equals(List.of("dexterity")), "/savingThrow");
        return assembly.claim("saving-throw", "fireball-save-v1",
                new SavingThrowRequirementMechanic(new FixedAbility(Ability.DEXTERITY),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)), unknownContext(), List.of(
                        MechanicEvidenceAssociation.of(source.textEvidence(BODY, SAVE), "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(source.fieldEvidence("/savingThrow/0"),
                                "/payload/ability", EvidenceRole.VALUE)), false, MechanicsCompleteness.PARTIAL);
    }

    private MechanicRecord damage(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                  SpellMechanicsAssembly assembly, MechanicRecord save) {
        require(save != null, "/savingThrow");
        MarkupTag tag = fireballBody(spell, source);
        require(spell.mechanics().damageInflicted().equals(List.of("fire")), "/damageInflict");
        String role = damageRole(source.text(BODY).text(), tag, "fire");
        require(role != null, BODY);
        DamageSaveOutcomeEvidence outcomes = saveOutcomeEvidence(save, source, role, BODY);
        return assembly.claim("failed-save-damage", "fireball-damage-v1",
                new DamageMechanic(dice(tag, BODY), new FixedDamageType(spell.mechanics().damageInflicted().getFirst())),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), outcomes.bindings()),
                combineEvidence(source.textEvidence(BODY, role), outcomes.evidence()),
                false, MechanicsCompleteness.PARTIAL);
    }

    private MarkupTag fireballBody(SpellDefinition spell, SpellMechanicsEvidenceSource source) {
        require(spell.entries().size() == 2 && spell.entries().getFirst() instanceof TextEntryDte, "/entries");
        MarkupTag tag = singleFormula(source, "damage");
        String prefix = FIREBALL_INTRO + "Each creature in a " + AREA + " centered on that point " + SAVE
                + ". A target takes " + tag.originalText() + " fire damage " + FAILURE + ", or ";
        String body = source.text(BODY).text();
        require(body.startsWith(prefix) && (body.substring(prefix.length()).equals(SUCCESS + ".")
                || body.substring(prefix.length()).equals("no damage on a successful one.")), BODY);
        return tag;
    }

    private MechanicRecord healing(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                   SpellMechanicsAssembly assembly) {
        require(spell.entries().size() == 1 && spell.entries().getFirst() instanceof TextEntryDte, "/entries");
        MarkupTag tag = singleFormula(source, "dice");
        String role = "regains hit points equal to " + tag.originalText() + HEALING_MODIFIER;
        require(source.text(BODY).text().equals(HEALING_PREFIX + role + ". " + HEALING_EXCLUSIONS), BODY);
        return assembly.claim("healing", "healing-word-amount-v1",
                new HealingMechanic(new SumAmount(List.of(dice(tag, BODY),
                        new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER))),
                        HealingMode.RESTORE_HIT_POINTS), unknownContext(), List.of(
                        MechanicEvidenceAssociation.of(source.textEvidence(BODY, role),
                                "/payload", EvidenceRole.EFFECT_ROLE)), false, MechanicsCompleteness.PARTIAL);
    }

    static MarkupTag singleFormula(SpellMechanicsEvidenceSource source, String name) {
        return singleFormula(source, BODY, name);
    }

    static MarkupTag singleFormula(SpellMechanicsEvidenceSource source, String pointer, String name) {
        try {
            List<MarkupTag> tags = new BalancedMarkupParser().parse(source.text(pointer)).tags();
            require(tags.size() == 1, pointer);
            MarkupTag tag = tags.getFirst();
            require(tag.name().equals(name) && tag.arguments().size() == 1 && tag.children().isEmpty(), pointer);
            dice(tag, pointer);
            return tag;
        } catch (MarkupSyntaxException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, pointer);
        }
    }

    static DiceAmount dice(MarkupTag tag, String pointer) {
        return diceExpression(tag.arguments().getFirst(), pointer);
    }

    private static DiceAmount diceExpression(String expression, String pointer) {
        Matcher match = DICE.matcher(expression);
        require(match.matches(), pointer);
        try {
            return new DiceAmount(Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)), 0);
        } catch (NumberFormatException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, pointer);
        }
    }

    private void defer(SpellMechanicsAssembly assembly, SpellMechanicsEvidenceSource source,
                       String id, MechanicFamily family, String pointer, String phrase) {
        try {
            assembly.unsupported(id, family, List.of(source.textEvidence(pointer, phrase).id()));
        } catch (SpellPatternMismatch mismatch) {
            assembly.abstain(id, family, mismatch.reason(), mismatch.relativePointer());
        }
    }

    static MechanicContext unknownContext() {
        return new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of());
    }

    static void require(boolean matches, String pointer) {
        if (!matches) {
            throw new SpellPatternMismatch(UnsupportedReason.NO_SUPPORTED_PATTERN, pointer);
        }
    }

    static MechanicEvidenceAssociation association(MechanicsEvidence evidence, String path,
                                                           EvidenceRole role) {
        return MechanicEvidenceAssociation.of(evidence, path, role);
    }

    private static void verifyEntries(List<EntryDte> entries, String pointer, SpellMechanicsEvidenceSource source) {
        source.verifyListSize(pointer, entries.size());
        for (int index = 0; index < entries.size(); index++) {
            String path = pointer + "/" + index;
            if (entries.get(index) instanceof TextEntryDte text) {
                source.verifyText(path, text.text());
            } else if (entries.get(index) instanceof SectionEntryDte section) {
                source.verifyText(path + "/name", section.name());
                source.verifyText(path + "/id", section.id());
                verifyEntries(section.entries(), path + "/entries", source);
            } else {
                ModelChecks.require(!source.node(path).isTextual(), "Normalized entry shape differs from source");
            }
        }
    }
}
