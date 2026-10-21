package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellScalingValue;
import com.thedndpub.data.mechanics.model.AmountExpression;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.GenericScalingMechanic;
import com.thedndpub.data.mechanics.model.IntegerAmount;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.ScalingAxis;
import com.thedndpub.data.mechanics.model.ScalingProgressionType;
import com.thedndpub.data.mechanics.model.ScalingQuantity;
import com.thedndpub.data.mechanics.model.ScalingStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Extracts bounded source-authored level progressions without evaluating them. */
public final class GenericScalingExtractionStrategy implements MechanicExtractionStrategy {
    private static final Pattern SCALED_MACRO = Pattern.compile(
            "(?i)\\{@scale(?:damage|dice)\\s+([^|}]+)\\|([^|}]+)\\|([^}]+)}");
    private static final Pattern ABOVE_LEVEL = Pattern.compile(
            "(?i)\\b(?:slot level|spell slot level) above (?:the )?(\\d+)(?:st|nd|rd|th)?\\b");
    private static final Pattern FIXED_SLOT_LEVEL = Pattern.compile(
            "(?i)\\b(?:at|using|with)\\s+(?:a\\s+)?(\\d+)(?:st|nd|rd|th)[- ]level\\s+(?:spell\\s+)?slot\\b");
    private static final Pattern AMOUNT_TAG = Pattern.compile("(?i)\\{@(?:damage|dice)\\s+([^}]+)}");
    private static final Pattern ADDITIONAL_COUNT = Pattern.compile(
            "(?i)\\b(one|two|three|four|five|six|seven|eight|nine|ten|\\d+)\\s+(?:additional|more)\\s+"
                    + "(ray|beam|dart|missile|attack|target|creature)s?\\b");
    private static final Pattern COUNT_TIER = Pattern.compile(
            "(?i)\\b(one|two|three|four|five|six|seven|eight|nine|ten|\\d+)\\s+"
                    + "(rays?|beams?|darts?|missiles?|attacks?|targets?|creatures?)\\s+at "
                    + "(?:(?:the )?(?:character )?level\\s+)?(\\d+)(?:st|nd|rd|th)?(?:\\s+level)?\\b");
    private static final Pattern ORDINAL = Pattern.compile(
            "(?i)\\b(?:when you reach |at )?(\\d+)(?:st|nd|rd|th) level\\b");
    private static final Pattern DICE = Pattern.compile("(?i)(\\d+)\\s*d\\s*(\\d+)(?:\\s*([+-])\\s*(\\d+))?");
    private static final Pattern INTEGER = Pattern.compile("\\d+");

    @Override public String strategyId() { return "generic-level-scaling"; }
    @Override public String version() { return "1"; }
    @Override public Set<MechanicType> supportedTypes() { return Set.of(MechanicType.SCALING); }

    @Override
    public StrategyExtraction extract(MechanicCandidate candidate, SourceFragment fragment, SpellDefinition spell) {
        if (fragment.kind() == SourceFragment.Kind.STRUCTURED_FIELD
                && fragment.pointer().equals("/scalingLevelDice")) {
            return structuredCharacterScaling(spell);
        }
        if (fragment.kind() != SourceFragment.Kind.AUTHORED_TEXT) {
            return StrategyExtraction.unsupported("SCALING_SOURCE_UNSUPPORTED",
                    "The structured source field does not match a supported level progression shape.");
        }
        if (spell != null && !spell.scaling().isEmpty() && isCharacterProgressionText(fragment.text())) {
            return StrategyExtraction.unsupported("SCALING_DUPLICATE_STRUCTURED_REPRESENTATION",
                    "Character-level scaling is represented by the pinned structured scaling field.");
        }
        String lower = fragment.text().toLowerCase(Locale.ROOT);
        if (isSlotProgression(lower)) {
            return slotScaling(fragment.text(), spell);
        }
        if (isCharacterProgressionText(fragment.text())) {
            return characterTextScaling(fragment.text());
        }
        return StrategyExtraction.unsupported("SCALING_PROGRESSION_UNRESOLVED",
                "Scaling language was recognized, but its axis, increment, threshold, or affected quantity is unresolved.");
    }

    private static StrategyExtraction structuredCharacterScaling(SpellDefinition spell) {
        if (spell.scaling().size() != 1 || spell.scaling().getFirst().values().isEmpty()) {
            return StrategyExtraction.unsupported("SCALING_STRUCTURED_VALUES_UNSUPPORTED",
                    "Structured character-level scaling is empty or has multiple independent labels.");
        }
        var source = spell.scaling().getFirst();
        ScalingQuantity quantity = quantityFromLabel(source.label());
        List<ScalingStep> steps = new ArrayList<>();
        for (SpellScalingValue value : source.values().stream()
                .sorted(Comparator.comparingInt(item -> parseLevel(item.levelKey()))).toList()) {
            int level = parseLevel(value.levelKey());
            AmountExpression amount = parseAmount(value.formula());
            if (amount == null) {
                return StrategyExtraction.unsupported("SCALING_VALUE_UNSUPPORTED",
                        "A structured scaling tier is not a supported dice or integer amount.");
            }
            steps.add(new ScalingStep(level, amount));
        }
        return single(new GenericScalingMechanic(ScalingAxis.CHARACTER_LEVEL,
                ScalingProgressionType.THRESHOLD_VALUE, quantity, 1, steps));
    }

    private static StrategyExtraction slotScaling(String text, SpellDefinition spell) {
        ScalingQuantity quantity = quantityFromText(text);
        Matcher macro = SCALED_MACRO.matcher(text);
        AmountExpression increment = null;
        if (macro.find()) {
            increment = parseAmount(macro.group(3));
            if (macro.find()) {
                return StrategyExtraction.unsupported("SCALING_MULTIPLE_PROGRESSION_AMOUNTS",
                        "A single fragment contains multiple scaling macros with unresolved relationships.");
            }
        } else {
            Matcher count = ADDITIONAL_COUNT.matcher(text);
            if (count.find()) {
                increment = new IntegerAmount(parseCount(count.group(1)));
                if (count.find()) {
                    return StrategyExtraction.unsupported("SCALING_MULTIPLE_PROGRESSION_AMOUNTS",
                            "A single fragment changes multiple counts with unresolved relationships.");
                }
            }
        }
        Matcher threshold = ABOVE_LEVEL.matcher(text);
        if (increment == null || !threshold.find()) {
            StrategyExtraction fixed = fixedSlotScaling(text, quantity, spell);
            if (fixed != null) return fixed;
            return StrategyExtraction.unsupported("SCALING_PROGRESSION_UNRESOLVED",
                    "The spell-slot clause must state a supported increment and the level above which it applies.");
        }
        int base = Integer.parseInt(threshold.group(1));
        int minimum = base + 1;
        if (base > 9 || minimum > 9) {
            return StrategyExtraction.unsupported("SCALING_THRESHOLD_INVALID",
                    "The authored spell-slot threshold is outside spell-slot levels 1 through 9.");
        }
        return single(new GenericScalingMechanic(ScalingAxis.SPELL_SLOT_LEVEL,
                ScalingProgressionType.PER_LEVEL_INCREMENT, quantity, base,
                List.of(new ScalingStep(minimum, increment))));
    }

    private static StrategyExtraction fixedSlotScaling(String text, ScalingQuantity quantity, SpellDefinition spell) {
        Matcher levels = FIXED_SLOT_LEVEL.matcher(text);
        List<ScalingStep> steps = new ArrayList<>();
        ScalingProgressionType type = null;
        int previousLevel = 0;
        while (levels.find()) {
            int level = Integer.parseInt(levels.group(1));
            int start = Math.max(text.lastIndexOf(';', levels.start()), text.lastIndexOf('.', levels.start())) + 1;
            Matcher next = FIXED_SLOT_LEVEL.matcher(text);
            if (next.find(levels.end())) {
                start = Math.max(start, text.lastIndexOf(';', levels.start()) + 1);
            }
            int end = text.length();
            int nextClause = text.indexOf(';', levels.end());
            int nextPeriod = text.indexOf('.', levels.end());
            if (nextClause >= 0) end = Math.min(end, nextClause);
            if (nextPeriod >= 0) end = Math.min(end, nextPeriod);
            Matcher nextLevel = FIXED_SLOT_LEVEL.matcher(text);
            if (nextLevel.find(levels.end())) end = Math.min(end, nextLevel.start());
            String clause = text.substring(start, end);
            Matcher amountTag = AMOUNT_TAG.matcher(clause);
            if (!amountTag.find()) return null;
            AmountExpression amount = parseAmount(amountTag.group(1));
            if (amount == null || amountTag.find()) return null;
            ScalingProgressionType authoredType = clause.toLowerCase(Locale.ROOT).contains("increases by")
                    ? ScalingProgressionType.FIXED_THRESHOLD_INCREMENT
                    : clause.toLowerCase(Locale.ROOT).contains("increases to")
                            ? ScalingProgressionType.THRESHOLD_VALUE : null;
            if (authoredType == null || type != null && type != authoredType || level <= previousLevel) return null;
            type = authoredType;
            previousLevel = level;
            steps.add(new ScalingStep(level, amount));
        }
        if (steps.isEmpty()) return null;
        Integer baseLevel = spell == null || spell.level() == null || spell.level() < 1 ? null : spell.level();
        return single(new GenericScalingMechanic(ScalingAxis.SPELL_SLOT_LEVEL, type, quantity, baseLevel, steps));
    }

    private static StrategyExtraction characterTextScaling(String text) {
        ScalingQuantity quantity = quantityFromText(text);
        if (quantity == ScalingQuantity.UNSPECIFIED_NUMERIC_QUANTITY) {
            return StrategyExtraction.unsupported("SCALING_QUANTITY_UNRESOLVED",
                    "Character-level scaling does not identify a supported affected quantity.");
        }
        Matcher countTiers = COUNT_TIER.matcher(text);
        List<ScalingStep> countSteps = new ArrayList<>();
        ScalingQuantity countQuantity = null;
        while (countTiers.find()) {
            ScalingQuantity tierQuantity = countQuantity(countTiers.group(2), text);
            if (countQuantity != null && countQuantity != tierQuantity) {
                return StrategyExtraction.unsupported("SCALING_MULTIPLE_QUANTITIES_UNSUPPORTED",
                        "Character-level tiers change different quantities in one fragment.");
            }
            countQuantity = tierQuantity;
            countSteps.add(new ScalingStep(Integer.parseInt(countTiers.group(3)),
                    new IntegerAmount(parseCount(countTiers.group(1)))));
        }
        if (!countSteps.isEmpty()) {
            return single(new GenericScalingMechanic(ScalingAxis.CHARACTER_LEVEL,
                    ScalingProgressionType.THRESHOLD_VALUE, countQuantity, 1, countSteps));
        }
        return StrategyExtraction.unsupported("SCALING_CHARACTER_TEXT_UNSUPPORTED",
                "Character-level progression was recognized but no explicit tier values were resolved.");
    }

    private static boolean isSlotProgression(String lower) {
        return lower.contains("slot level above") || lower.contains("spell slot level above")
                || lower.contains("using a spell slot") && lower.contains("increases by")
                || FIXED_SLOT_LEVEL.matcher(lower).find();
    }

    private static boolean isCharacterProgressionText(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        return lower.contains("when you reach") || lower.contains("at 5th level")
                || lower.contains("at level 5") || COUNT_TIER.matcher(text).find();
    }

    private static ScalingQuantity quantityFromText(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.matches("(?s).*\\bhealing\\b.*")) return ScalingQuantity.HEALING_AMOUNT;
        if (lower.matches("(?s).*\\bdamage\\b.*")) return ScalingQuantity.DAMAGE_AMOUNT;
        if (lower.matches("(?s).*\\b(?:attack|attack roll)s?\\b.*")) return ScalingQuantity.ATTACK_COUNT;
        if (lower.matches("(?s).*\\b(?:ray|beam|dart|missile|projectile)s?\\b.*")) {
            return ScalingQuantity.PROJECTILE_COUNT;
        }
        if (lower.matches("(?s).*\\b(?:target|creature)s?\\b.*")) return ScalingQuantity.TARGET_COUNT;
        return ScalingQuantity.UNSPECIFIED_NUMERIC_QUANTITY;
    }

    private static ScalingQuantity quantityFromLabel(String label) {
        String lower = label.toLowerCase(Locale.ROOT);
        if (lower.contains("damage")) return ScalingQuantity.DAMAGE_AMOUNT;
        if (lower.contains("healing")) return ScalingQuantity.HEALING_AMOUNT;
        if (lower.contains("attack")) return ScalingQuantity.ATTACK_COUNT;
        if (lower.matches("(?s).*(beam|ray|dart|missile|projectile).*")) return ScalingQuantity.PROJECTILE_COUNT;
        if (lower.contains("target")) return ScalingQuantity.TARGET_COUNT;
        return ScalingQuantity.UNSPECIFIED_NUMERIC_QUANTITY;
    }

    private static ScalingQuantity countQuantity(String noun, String text) {
        String lower = noun.toLowerCase(Locale.ROOT);
        if (lower.startsWith("attack")) return ScalingQuantity.ATTACK_COUNT;
        if (lower.startsWith("target") || lower.startsWith("creature")) return ScalingQuantity.TARGET_COUNT;
        if (text.toLowerCase(Locale.ROOT).contains("attack roll for each " + lower.replaceAll("s$", ""))) {
            return ScalingQuantity.ATTACK_COUNT;
        }
        return ScalingQuantity.PROJECTILE_COUNT;
    }

    private static int parseLevel(String value) {
        try {
            int level = Integer.parseInt(value);
            if (level < 1 || level > 20) throw new IllegalArgumentException("Scaling level outside supported bounds");
            return level;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Scaling level is not numeric", exception);
        }
    }

    private static int parseCount(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "one" -> 1; case "two" -> 2; case "three" -> 3; case "four" -> 4; case "five" -> 5;
            case "six" -> 6; case "seven" -> 7; case "eight" -> 8; case "nine" -> 9; case "ten" -> 10;
            default -> Integer.parseInt(value);
        };
    }

    private static AmountExpression parseAmount(String text) {
        String value = text.strip().replaceAll("\\{@[^ ]+ ", "").replace("}", "").strip();
        Matcher dice = DICE.matcher(value);
        if (dice.matches()) {
            int modifier = dice.group(4) == null ? 0 : Integer.parseInt(dice.group(4));
            if (dice.group(3) != null && dice.group(3).equals("-")) modifier = -modifier;
            return new DiceAmount(Integer.parseInt(dice.group(1)), Integer.parseInt(dice.group(2)), modifier);
        }
        Matcher integer = INTEGER.matcher(value);
        if (integer.matches()) return new IntegerAmount(Integer.parseInt(value));
        return null;
    }

    private static StrategyExtraction single(MechanicPayload payload) {
        return new StrategyExtraction(payload, null, null);
    }
}
