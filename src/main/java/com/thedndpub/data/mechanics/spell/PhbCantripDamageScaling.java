package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellScaling;
import com.thedndpub.data.dto.spell.SpellScalingValue;
import com.thedndpub.data.mechanics.model.CharacterLevelDamageScaling;
import com.thedndpub.data.mechanics.model.CharacterLevelDamageTier;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Validates the source-structured and prose forms of the bounded PHB cantrip damage progression. */
final class PhbCantripDamageScaling {
    private static final List<Integer> LEVELS = List.of(1, 5, 11, 17);
    private static final Pattern DICE = Pattern.compile("([1-9][0-9]*)d([1-9][0-9]*)");
    private static final Pattern INCREASES = Pattern.compile("(?i)(?:the|this) spell's damage increases by "
            + "\\{@damage ([^}]+)} when you reach ([0-9]+)(st|nd|rd|th) level");
    private static final Pattern DAMAGE_TAG = Pattern.compile("\\{@damage ([^}]+)}");

    private PhbCantripDamageScaling() {
    }

    static Extraction extract(
            SpellDefinition spell,
            String textPointer,
            String expectedLabel,
            DiceAmount baseAmount,
            SpellMechanicsEvidenceSource source) {
        require(spell.level() != null && spell.level() == 0, "/level");
        List<SpellScaling> sourceScaling = spell.scaling();
        CharacterLevelDamageScaling progression = parse(
                sourceScaling, expectedLabel, source.text(textPointer).text(), baseAmount);
        verifyStructuredSource(source, sourceScaling);

        var associations = new ArrayList<MechanicEvidenceAssociation>();
        StructuredFieldMechanicsEvidence labelEvidence = source.fieldEvidence("/scalingLevelDice/label");
        associations.add(MechanicEvidenceAssociation.of(labelEvidence,
                "/payload/characterLevelScaling", EvidenceRole.QUALIFIER));
        StructuredFieldMechanicsEvidence baseEvidence = source.fieldEvidence("/scalingLevelDice/scaling/1");
        associations.add(MechanicEvidenceAssociation.of(baseEvidence, "/payload/amount", EvidenceRole.VALUE));

        String prose = source.text(textPointer).text();
        Matcher increase = INCREASES.matcher(prose);
        require(increase.find(), textPointer);
        TextMechanicsEvidence incrementEvidence = source.textEvidence(textPointer, increase.group());
        associations.add(MechanicEvidenceAssociation.of(incrementEvidence,
                "/payload/characterLevelScaling", EvidenceRole.SCALING));

        List<SpellScalingValue> values = orderedValues(sourceScaling);
        for (int index = 1; index < values.size(); index++) {
            SpellScalingValue value = values.get(index);
            int level = Integer.parseInt(value.levelKey());
            String fieldPointer = "/scalingLevelDice/scaling/" + value.levelKey();
            String tierPath = "/payload/characterLevelScaling/tiers/" + (index - 1);
            StructuredFieldMechanicsEvidence structured = source.fieldEvidence(fieldPointer);
            associations.add(MechanicEvidenceAssociation.of(structured,
                    tierPath + "/minimumCharacterLevel", EvidenceRole.VALUE));
            associations.add(MechanicEvidenceAssociation.of(structured, tierPath + "/amount", EvidenceRole.VALUE));
            TextMechanicsEvidence text = source.textEvidence(textPointer,
                    ordinal(level) + " level ({@damage " + value.formula() + "})");
            associations.add(MechanicEvidenceAssociation.of(text, tierPath, EvidenceRole.QUALIFIER));
        }
        return new Extraction(progression, associations);
    }

    static CharacterLevelDamageScaling parse(
            List<SpellScaling> scaling,
            String expectedLabel,
            String authoredText,
            DiceAmount baseAmount) {
        require(scaling != null && scaling.size() == 1, "/scalingLevelDice");
        SpellScaling entry = scaling.getFirst();
        require(expectedLabel.equals(entry.label()), "/scalingLevelDice/label");
        List<SpellScalingValue> values = orderedValues(scaling);
        require(values.stream().map(value -> parseLevel(value.levelKey())).toList().equals(LEVELS),
                "/scalingLevelDice/scaling");

        List<DiceAmount> amounts = new ArrayList<>();
        for (SpellScalingValue value : values) {
            amounts.add(parseDice(value.formula(), "/scalingLevelDice/scaling/" + value.levelKey()));
        }
        require(amounts.getFirst().equals(baseAmount), "/scalingLevelDice/scaling/1");
        for (int index = 1; index < amounts.size(); index++) {
            DiceAmount previous = amounts.get(index - 1);
            DiceAmount current = amounts.get(index);
            require(current.sides() == previous.sides() && current.modifier() == previous.modifier()
                            && current.count() == previous.count() + 1,
                    "/scalingLevelDice/scaling/" + values.get(index).levelKey());
        }
        verifyProse(values, amounts, authoredText);

        List<CharacterLevelDamageTier> tiers = new ArrayList<>();
        for (int index = 1; index < values.size(); index++) {
            tiers.add(new CharacterLevelDamageTier(parseLevel(values.get(index).levelKey()), amounts.get(index)));
        }
        return new CharacterLevelDamageScaling(tiers);
    }

    private static void verifyStructuredSource(
            SpellMechanicsEvidenceSource source,
            List<SpellScaling> scaling) {
        JsonNode root = source.node("/scalingLevelDice");
        require(root.isObject() && root.size() == 2, "/scalingLevelDice");
        JsonNode label = root.get("label");
        JsonNode levels = root.get("scaling");
        require(label != null && label.isTextual() && levels != null && levels.isObject(), "/scalingLevelDice");
        List<SpellScalingValue> values = orderedValues(scaling);
        require(levels.size() == values.size(), "/scalingLevelDice/scaling");
        for (SpellScalingValue value : values) {
            JsonNode formula = levels.get(value.levelKey());
            require(formula != null && formula.isTextual() && formula.textValue().equals(value.formula()),
                    "/scalingLevelDice/scaling/" + value.levelKey());
        }
        source.verifyText("/scalingLevelDice/label", scaling.getFirst().label());
        for (SpellScalingValue value : values) {
            source.verifyText("/scalingLevelDice/scaling/" + value.levelKey(), value.formula());
        }
    }

    private static List<SpellScalingValue> orderedValues(List<SpellScaling> scaling) {
        if (scaling == null || scaling.size() != 1 || scaling.getFirst() == null
                || scaling.getFirst().values() == null) {
            throw new SpellPatternMismatch(com.thedndpub.data.mechanics.model.UnsupportedReason.NO_SUPPORTED_PATTERN,
                    "/scalingLevelDice");
        }
        List<SpellScalingValue> values = scaling.getFirst().values();
        require(!values.isEmpty() && values.stream().allMatch(java.util.Objects::nonNull),
                "/scalingLevelDice/scaling");
        return values.stream()
                .sorted(Comparator.comparingInt(value -> parseLevel(value.levelKey())))
                .toList();
    }

    private static void verifyProse(
            List<SpellScalingValue> values,
            List<DiceAmount> amounts,
            String authoredText) {
        Matcher increase = INCREASES.matcher(authoredText);
        require(increase.find(), "/entries/1");
        int firstThreshold = parseLevel(increase.group(2));
        require(firstThreshold == parseLevel(values.get(1).levelKey()), "/entries/1");
        DiceAmount previous = amounts.getFirst();
        DiceAmount firstTier = amounts.get(1);
        String increment = (firstTier.count() - previous.count()) + "d" + firstTier.sides();
        require(increase.group(1).equals(increment), "/entries/1");
        require(!increase.find(), "/entries/1");

        List<String> authoredDice = new ArrayList<>();
        Matcher diceTags = DAMAGE_TAG.matcher(authoredText);
        while (diceTags.find()) {
            authoredDice.add(diceTags.group(1));
        }
        List<String> expectedDice = new ArrayList<>();
        expectedDice.add(increment);
        for (int index = 1; index < amounts.size(); index++) {
            expectedDice.add(values.get(index).formula());
            int level = parseLevel(values.get(index).levelKey());
            require(occurrences(authoredText,
                            ordinal(level) + " level ({@damage " + values.get(index).formula() + "})") == 1,
                    "/entries/1");
        }
        require(authoredDice.equals(expectedDice), "/entries/1");
    }

    private static DiceAmount parseDice(String formula, String pointer) {
        Matcher matcher = DICE.matcher(formula == null ? "" : formula);
        require(matcher.matches(), pointer);
        try {
            return new DiceAmount(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), 0);
        } catch (NumberFormatException exception) {
            throw new SpellPatternMismatch(
                    com.thedndpub.data.mechanics.model.UnsupportedReason.UNSUPPORTED_SYNTAX, pointer);
        }
    }

    private static int parseLevel(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException | NullPointerException exception) {
            throw new SpellPatternMismatch(
                    com.thedndpub.data.mechanics.model.UnsupportedReason.UNSUPPORTED_SYNTAX,
                    "/scalingLevelDice/scaling");
        }
    }

    private static int occurrences(String text, String phrase) {
        int count = 0;
        int start = 0;
        while ((start = text.indexOf(phrase, start)) >= 0) {
            count++;
            start += phrase.length();
        }
        return count;
    }

    private static String ordinal(int level) {
        int lastTwoDigits = level % 100;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 13) {
            return level + "th";
        }
        return switch (level % 10) {
            case 1 -> level + "st";
            case 2 -> level + "nd";
            case 3 -> level + "rd";
            default -> level + "th";
        };
    }

    private static void require(boolean condition, String pointer) {
        if (!condition) {
            throw new SpellPatternMismatch(
                    com.thedndpub.data.mechanics.model.UnsupportedReason.AMBIGUOUS_INTERPRETATION, pointer);
        }
    }

    record Extraction(
            CharacterLevelDamageScaling progression,
            List<MechanicEvidenceAssociation> evidenceAssociations) {
        Extraction {
            evidenceAssociations = List.copyOf(evidenceAssociations);
        }
    }
}
