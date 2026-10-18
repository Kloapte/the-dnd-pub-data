package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HealingMode;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
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
            "spell:phb:circle-of-death");
    private static final Set<String> HEALING_OWNERS = Set.of(
            "spell:phb:healing-word",
            "spell:phb:cure-wounds",
            "spell:phb:mass-cure-wounds",
            "spell:phb:mass-healing-word",
            "spell:phb:prayer-of-healing");
    private static final Set<String> PREVIOUSLY_PROCESSED_OWNERS = Set.of(
            "spell:phb:fireball", "spell:phb:healing-word");
    private static final Set<String> OWNERS;
    private static final String BODY = "/entries/0";
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

    static {
        var owners = new java.util.HashSet<>(DAMAGE_OWNERS);
        owners.addAll(HEALING_OWNERS);
        OWNERS = Set.copyOf(owners);
    }

    @Override
    public String patternId() {
        return "phb-damage-and-healing-v1";
    }

    @Override
    public boolean supports(SpellDefinition spell) {
        return OWNERS.contains(spell.id().value());
    }

    @Override
    public MechanicsDocument extract(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                     MechanicsDocument original) {
        ModelChecks.require(supports(spell), "Unsupported PHB extraction owner");
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
        String adapterId = PREVIOUSLY_PROCESSED_OWNERS.contains(spell.id().value())
                ? "phb-spell-pilot" : "phb-spell-patterns";
        var assembly = new SpellMechanicsAssembly(source, original, adapterId);
        if (spell.id().value().equals("spell:phb:fireball")) {
            MechanicRecord save = assembly.attempt("fireball-save-v1", MechanicFamily.SAVING_THROW_REQUIREMENT,
                    () -> savingThrow(spell, source, assembly));
            assembly.attempt("fireball-damage-v1", MechanicFamily.DAMAGE,
                    () -> damage(spell, source, assembly, save));
            defer(assembly, source, "successful-save-half", MechanicFamily.DAMAGE, BODY, SUCCESS);
            defer(assembly, source, "area-targets", MechanicFamily.UNDETERMINED, BODY, AREA);
            defer(assembly, source, "slot-scaling", MechanicFamily.DAMAGE, HIGHER, null);
        } else if (spell.id().value().equals("spell:phb:healing-word")) {
            assembly.attempt("healing-word-amount-v1", MechanicFamily.HEALING,
                    () -> healing(spell, source, assembly));
            defer(assembly, source, "target-restrictions", MechanicFamily.HEALING, BODY, null);
            defer(assembly, source, "slot-scaling", MechanicFamily.HEALING, HIGHER, null);
        } else if (DAMAGE_OWNERS.contains(spell.id().value())) {
            extractGeneralDamage(spell, source, assembly);
        } else {
            extractGeneralHealing(spell, source, assembly);
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
        MechanicRecord save = assembly.attempt("save-requirement-v1", MechanicFamily.SAVING_THROW_REQUIREMENT,
                () -> generalSavingThrow(spell, source, assembly));
        assembly.attempt("damage-on-failed-save-v1", MechanicFamily.DAMAGE,
                () -> generalFailedSaveDamage(spell, source, assembly, save));
        String halfDamage = findHalfDamageClause(source.text(BODY).text());
        if (halfDamage != null) {
            defer(assembly, source, "successful-save-half", MechanicFamily.DAMAGE, BODY, halfDamage);
        } else {
            assembly.abstain("successful-save-outcome", MechanicFamily.DAMAGE,
                    UnsupportedReason.NO_SUPPORTED_PATTERN, BODY);
        }
    }

    private MechanicRecord generalSavingThrow(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        String text = source.text(BODY).text();
        Matcher matcher = Pattern.compile(
                "\\bmust make a (Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma) saving throw\\b")
                .matcher(text);
        require(matcher.find(), BODY);
        String authoredAbility = matcher.group(1);
        require(!matcher.find(), BODY);
        List<String> abilities = spell.mechanics().savingThrows();
        require(abilities != null && abilities.size() == 1, "/savingThrow");
        Ability ability = ability(abilities.getFirst());
        require(authoredAbility.equalsIgnoreCase(ability.name()), BODY);
        TextMechanicsEvidence saveText = source.textEvidence(BODY, authoredAbility + " saving throw");
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
            MechanicRecord save) {
        require(save != null, BODY);
        String text = source.text(BODY).text();
        MarkupTag tag = singleFormula(source, "damage");
        List<String> types = spell.mechanics().damageInflicted();
        require(types != null && types.size() == 1, "/damageInflict");
        String type = types.getFirst();
        String role = damageRole(text, tag, type);
        require(role != null, BODY);
        requireSaveAndDamageAreLocallyRelated(text, tag);
        Matcher failedSave = Pattern.compile("(?i)on a failed save").matcher(role);
        require(failedSave.find(), BODY);
        var outcome = source.textEvidence(BODY, failedSave.group());
        TextMechanicsEvidence damageText = source.textEvidence(BODY, role);
        return assembly.claim("failed-save-damage", "damage-on-failed-save-v1",
                new DamageMechanic(dice(tag, BODY), new FixedDamageType(type)),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of(
                        new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.FAILURE,
                                List.of(outcome.id())))), List.of(
                        MechanicEvidenceAssociation.of(damageText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(outcome, "/context/bindings/0", EvidenceRole.QUALIFIER)),
                false, MechanicsCompleteness.PARTIAL);
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
                + " damage(?: on a failed save)?(?:, or half as much damage on a successful (?:save|one))?")
                .matcher(clause).matches();
        if (!recognized || !clause.toLowerCase(java.util.Locale.ROOT).contains("on a failed save")) {
            return null;
        }
        return clause;
    }

    private void requireSaveAndDamageAreLocallyRelated(String text, MarkupTag tag) {
        Matcher savingThrow = Pattern.compile(
                "\\bmust make a (?:Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma) saving throw\\b")
                .matcher(text);
        require(savingThrow.find(), BODY);
        int saveEnd = savingThrow.end();
        require(!savingThrow.find(), BODY);
        int formulaStart = text.indexOf(tag.originalText());
        require(formulaStart > saveEnd, BODY);
        String between = text.substring(saveEnd, formulaStart);
        require(between.chars().filter(character -> character == '.').count() <= 1, BODY);
    }

    private String findHalfDamageClause(String text) {
        Matcher matcher = Pattern.compile(
                        "(?i)(?:or )?half (?:as much|the) damage(?: on a successful (?:save|one))?")
                .matcher(text);
        if (!matcher.find()) {
            return null;
        }
        String clause = matcher.group();
        return text.indexOf(clause, matcher.end()) >= 0 ? null : clause;
    }

    private Ability ability(String authored) {
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
        var outcome = source.textEvidence(BODY, FAILURE);
        String role = "A target takes " + tag.originalText() + " fire damage " + FAILURE;
        return assembly.claim("failed-save-damage", "fireball-damage-v1",
                new DamageMechanic(dice(tag, BODY), new FixedDamageType(spell.mechanics().damageInflicted().getFirst())),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of(
                        new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.FAILURE,
                                List.of(outcome.id())))), List.of(
                        MechanicEvidenceAssociation.of(source.textEvidence(BODY, role), "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(outcome, "/context/bindings/0", EvidenceRole.QUALIFIER)),
                false, MechanicsCompleteness.PARTIAL);
    }

    private MarkupTag fireballBody(SpellDefinition spell, SpellMechanicsEvidenceSource source) {
        require(spell.entries().size() == 2 && spell.entries().getFirst() instanceof TextEntryDte, "/entries");
        MarkupTag tag = singleFormula(source, "damage");
        String expected = FIREBALL_INTRO + "Each creature in a " + AREA + " centered on that point " + SAVE
                + ". A target takes " + tag.originalText() + " fire damage " + FAILURE + ", or " + SUCCESS + ".";
        require(source.text(BODY).text().equals(expected), BODY);
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

    private MarkupTag singleFormula(SpellMechanicsEvidenceSource source, String name) {
        try {
            List<MarkupTag> tags = new BalancedMarkupParser().parse(source.text(BODY)).tags();
            require(tags.size() == 1, BODY);
            MarkupTag tag = tags.getFirst();
            require(tag.name().equals(name) && tag.arguments().size() == 1 && tag.children().isEmpty(), BODY);
            dice(tag, BODY);
            return tag;
        } catch (MarkupSyntaxException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, BODY);
        }
    }

    private DiceAmount dice(MarkupTag tag, String pointer) {
        Matcher match = DICE.matcher(tag.arguments().getFirst());
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

    private static MechanicContext unknownContext() {
        return new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of());
    }

    private static void require(boolean matches, String pointer) {
        if (!matches) {
            throw new SpellPatternMismatch(UnsupportedReason.NO_SUPPORTED_PATTERN, pointer);
        }
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
