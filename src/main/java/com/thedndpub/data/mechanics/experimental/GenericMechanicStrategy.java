package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AbilitySelection;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionName;
import com.thedndpub.data.mechanics.model.ConditionOperation;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.AmountExpression;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.IntegerAmount;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HealingMode;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SubjectReference;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownValueReason;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Conservative source-structure strategy shared across editions; unsupported qualifiers remain diagnostics. */
public final class GenericMechanicStrategy implements MechanicExtractionStrategy {
    private static final Pattern DAMAGE_DICE = Pattern.compile("\\{@damage\\s+([^}]+)}", Pattern.CASE_INSENSITIVE);
    private static final Pattern DICE = Pattern.compile(
            "(\\d+)d(\\d+)(?:\\s*([+-])\\s*(\\d+))?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DAMAGE_TYPE = Pattern.compile("(?i)^\\s*(bludgeoning|piercing|slashing|acid|cold|"
            + "fire|force|lightning|necrotic|poison|psychic|radiant|thunder)\\s+damage\\b");
    private static final Pattern SAVE = Pattern.compile("(?i)(strength|dexterity|constitution|intelligence|wisdom|charisma)\\s+saving throw");
    private static final Pattern ATTACK = Pattern.compile("(?i)\\b(melee|ranged) spell attacks?\\b");
    private static final Pattern ATTACK_COUNT = Pattern.compile(
            "(?i)\\b(\\d+|one|two|three|four|five|six|seven|eight|nine|ten)\\s+(?:independent\\s+)?"
                    + "(?:melee|ranged) spell attacks?\\b");
    private static final Pattern EACH_PROJECTILE = Pattern.compile(
            "(?i)\\bfor each (ray|missile|dart|beam)\\b");
    private static final Pattern PROJECTILE_COUNT = Pattern.compile(
            "(?i)\\b(?:create|conjure|form|produce)\\s+(\\d+|one|two|three|four|five|six|seven|eight|nine|ten)"
                    + "\\b[^.;]{0,80}\\b(rays?|missiles?|darts?|beams?)\\b");
    private final MechanicType type;

    public GenericMechanicStrategy(MechanicType type) { this.type = type; }

    @Override public String strategyId() { return "generic-" + type.name().toLowerCase(Locale.ROOT); }
    @Override public String version() { return "1"; }
    @Override public Set<MechanicType> supportedTypes() { return Set.of(type); }

    @Override
    public StrategyExtraction extract(MechanicCandidate candidate, SourceFragment fragment, SpellDefinition spell) {
        String text = fragment.text();
        StrategyExtraction extraction = switch (type) {
            case ATTACK -> attack(text);
            case SAVING_THROW -> save(text, fragment);
            case DAMAGE -> damage(text, fragment);
            case HEALING -> single(healing(text));
            case CONDITION -> single(condition(fragment));
            case CONDITION_TERMINATION -> StrategyExtraction.unsupported("CONDITION_TERMINATION_STRATEGY_REQUIRED",
                    "Condition termination events require explicit source wording.");
            case DURATION -> StrategyExtraction.unsupported("CONDITION_DURATION_TEXT_REQUIRED",
                    "A duration signal was recognized, but its relationship to a condition was not extracted.");
            case SCALING -> StrategyExtraction.unsupported("INSUFFICIENT_SEMANTIC_RELATIONSHIP",
                    "Scaling was recognized, but the affected mechanic and progression are not yet supported.");
        };
        if (!extraction.supported()) {
            return extraction;
        }
        return extraction;
    }

    private static StrategyExtraction single(MechanicPayload payload) {
        return payload == null ? StrategyExtraction.unsupported("INSUFFICIENT_SEMANTIC_RELATIONSHIP",
                "Source fragment does not establish a supported typed mechanic.")
                : new StrategyExtraction(payload, null, null);
    }

    private static StrategyExtraction attack(String text) {
        Matcher matcher = ATTACK.matcher(text);
        if (!matcher.find()) return StrategyExtraction.unsupported("ATTACK_WORDING_UNSUPPORTED",
                "The source fragment does not establish a typed spell attack.");
        int attackPhraseStart = matcher.start();
        String attackType = matcher.group(1);
        if (matcher.find()) return StrategyExtraction.unsupported("AMBIGUOUS_ATTACK_GROUPING",
                "Multiple attack-kind phrases occur in one fragment and their grouping is unresolved.");
        AttackKind kind = attackType.equalsIgnoreCase("melee") ? AttackKind.MELEE_SPELL : AttackKind.RANGED_SPELL;
        String lower = text.toLowerCase(Locale.ROOT);
        int attackCount = 1;
        boolean independent = false;
        Matcher projectile = EACH_PROJECTILE.matcher(lower);
        if (projectile.find()) {
            Matcher group = PROJECTILE_COUNT.matcher(lower);
            boolean groupCountFound = false;
            int countGroups = 0;
            while (group.find()) {
                countGroups++;
                if (singular(group.group(2)).equals(singular(projectile.group(1)))) {
                    attackCount = count(group.group(1));
                    independent = attackCount > 1;
                    groupCountFound = true;
                    break;
                }
            }
            if (!groupCountFound && countGroups == 1) {
                Matcher soleGroup = PROJECTILE_COUNT.matcher(lower);
                if (soleGroup.find()) {
                    attackCount = count(soleGroup.group(1));
                    independent = attackCount > 1;
                    groupCountFound = true;
                }
            }
            if (!groupCountFound) {
                return StrategyExtraction.unsupported("AMBIGUOUS_ATTACK_GROUPING",
                        "An attack repeats for each projectile, but this source fragment does not establish the projectile count.");
            }
        }
        if (lower.matches("(?s).*\\bfor each (?:target|creature)\\b.*")
                || lower.matches("(?s).*\\bagainst each (?:target|creature)\\b.*")
                || lower.matches("(?s).*\\beach target\\b.*")) {
            return StrategyExtraction.unsupported("AMBIGUOUS_ATTACK_GROUPING",
                    "An attack is repeated for a grouped target or object set whose count is outside this fragment.");
        }
        if (!lower.matches("(?s).*\\b(?:make|makes|perform|performs|roll|rolls)\\b.*")) {
            return StrategyExtraction.unsupported("ATTACK_WORDING_UNSUPPORTED",
                    "The attack phrase is not connected to an authored attack action.");
        }
        Matcher countMatcher = ATTACK_COUNT.matcher(text);
        if (countMatcher.find()) {
            attackCount = count(countMatcher.group(1));
            independent = attackCount > 1;
        }
        String beforeAttack = lower.substring(0, attackPhraseStart);
        boolean explicitCaster = lower.matches("(?s).*\\b(?:you|the caster)\\s+(?:can )?(?:make|makes|perform|performs|roll|rolls)\\b.*")
                || beforeAttack.matches("(?s).*\\byou\\s+(?:create|conjure|form|produce)\\b.*");
        boolean explicitCastTiming = lower.contains("when you cast") || lower.contains("as part of casting")
                || lower.contains("when the spell is cast") || lower.contains("as you cast");
        AttackActorRole actor = explicitCaster ? AttackActorRole.CASTER : AttackActorRole.UNSPECIFIED;
        AttackActivationTiming timing = explicitCastTiming
                ? AttackActivationTiming.CAST_TIME : AttackActivationTiming.UNSPECIFIED;
        AttackRollMechanic payload = new AttackRollMechanic(kind, actor, attackCount, independent, timing);
        return new StrategyExtraction(List.of(new ExtractedMechanicPayload(payload,
                new MechanicContext(attackTarget(text), List.of()))), null, null);
    }

    private static int count(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return switch (value.toLowerCase(Locale.ROOT)) {
                case "one" -> 1;
                case "two" -> 2;
                case "three" -> 3;
                case "four" -> 4;
                case "five" -> 5;
                case "six" -> 6;
                case "seven" -> 7;
                case "eight" -> 8;
                case "nine" -> 9;
                case "ten" -> 10;
                default -> throw new IllegalArgumentException("Unsupported attack count");
            };
        }
    }

    private static SubjectReference attackTarget(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.matches("(?s).*\\b(?:each|every) (?:ray|missile|dart|beam) targets? (?:a|one) creature\\b.*")) {
            return new SpellTargetSubject(TargetScope.MULTIPLE_TARGET_CREATURES);
        }
        if (lower.matches("(?s).*\\b(?:against|at) (?:one|a) creature\\b.*")) {
            return new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE);
        }
        if (lower.matches("(?s).*\\bagainst (?:a|the) target\\b.*")) {
            return new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY);
        }
        return new UnknownSubject(UnknownValueReason.NOT_INTERPRETED);
    }

    private static String singular(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        return lower.endsWith("s") ? lower.substring(0, lower.length() - 1) : lower;
    }

    private static StrategyExtraction save(String text, SourceFragment fragment) {
        if (fragment.kind() != SourceFragment.Kind.AUTHORED_TEXT) {
            return StrategyExtraction.unsupported("SAVE_TEXT_RELATIONSHIP_REQUIRED",
                    "A structured save signal does not establish target or authored save semantics by itself.");
        }
        if (text.matches("(?is).*(strength|dexterity|constitution|intelligence|wisdom|charisma)\\s+or\\s+"
                + "(strength|dexterity|constitution|intelligence|wisdom|charisma)\\s+saving throw.*")) {
            return StrategyExtraction.unsupported("AMBIGUOUS_SAVE_ABILITY",
                    "Alternative saving throw abilities do not identify one typed save mechanic.");
        }
        Matcher matcher = SAVE.matcher(text);
        if (!matcher.find()) return StrategyExtraction.unsupported("SAVE_WORDING_UNSUPPORTED",
                "The source fragment does not contain a supported saving throw clause.");
        String abilityText = matcher.group(1);
        int saveStart = matcher.start();
        List<String> abilities = new ArrayList<>();
        abilities.add(abilityText.toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            abilities.add(matcher.group(1).toLowerCase(Locale.ROOT));
        }
        if (abilities.stream().distinct().count() != 1) {
            return StrategyExtraction.unsupported("AMBIGUOUS_SAVE_ABILITY",
                    "The source fragment contains multiple saving throw abilities.");
        }
        Ability ability = Ability.valueOf(abilityText.toUpperCase(Locale.ROOT));
        MechanicContext context = new MechanicContext(target(text, saveStart), List.of());
        return new StrategyExtraction(List.of(new ExtractedMechanicPayload(
                new SavingThrowRequirementMechanic(new FixedAbility(ability),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_INTERPRETED)), context)), null, null);
    }

    private static StrategyExtraction damage(String text, SourceFragment fragment) {
        if (fragment.kind() != SourceFragment.Kind.AUTHORED_TEXT) {
            return StrategyExtraction.unsupported("DAMAGE_TEXT_RELATIONSHIP_REQUIRED",
                    "Structured damage-type fields do not by themselves establish a direct damage event.");
        }
        Matcher matcher = DAMAGE_DICE.matcher(text);
        List<ExtractedMechanicPayload> payloads = new ArrayList<>();
        while (matcher.find()) {
            Matcher amountMatcher = DICE.matcher(matcher.group(1));
            if (!amountMatcher.matches()) {
                return StrategyExtraction.unsupported("DAMAGE_AMOUNT_UNSUPPORTED",
                        "Damage markup does not contain one supported dice amount.");
            }
            int suffixEnd = text.length();
            Matcher nextTag = DAMAGE_DICE.matcher(text);
            if (nextTag.find(matcher.end())) {
                suffixEnd = nextTag.start();
            }
            String suffix = text.substring(matcher.end(), suffixEnd);
            Matcher damageType = DAMAGE_TYPE.matcher(suffix);
            if (!damageType.find()) {
                continue;
            }
            int modifier = signedModifier(amountMatcher);
            DiceAmount amount = new DiceAmount(Integer.parseInt(amountMatcher.group(1)),
                    Integer.parseInt(amountMatcher.group(2)), modifier);
            payloads.add(new ExtractedMechanicPayload(new DamageMechanic(amount,
                    new FixedDamageType(damageType.group(1).toLowerCase(Locale.ROOT))),
                    new MechanicContext(target(text, matcher.start()), List.of()), matcher.start(), matcher.end()));
        }
        if (payloads.isEmpty()) {
            return StrategyExtraction.unsupported("DAMAGE_TYPE_OR_AMOUNT_UNRESOLVED",
                    "No source fragment established both a supported damage formula and an adjacent damage type.");
        }
        return new StrategyExtraction(payloads, null, null);
    }

    private static MechanicPayload healing(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        if (!(lower.contains("regain") && lower.contains("hit point"))) return null;
        Matcher matcher = DICE.matcher(text);
        if (!matcher.find()) return null;
        int firstStart = matcher.start();
        int firstEnd = matcher.end();
        DiceAmount amount = dice(matcher);
        if (matcher.find()) return null;
        String suffix = text.substring(firstEnd).replaceFirst("^\\s*}", "").stripLeading();
        AmountExpression expression = amount;
        if (suffix.startsWith("+")) {
            String added = suffix.substring(1).stripLeading().toLowerCase(Locale.ROOT);
            if (added.startsWith("your spellcasting ability modifier")) {
                expression = new SumAmount(List.of(amount,
                        new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER)));
            } else {
                Matcher integer = Pattern.compile("^(\\d+)\\b").matcher(added);
                if (!integer.find()) return null;
                expression = new SumAmount(List.of(amount, new IntegerAmount(Integer.parseInt(integer.group(1)))));
            }
        }
        return new HealingMechanic(expression, HealingMode.RESTORE_HIT_POINTS);
    }

    private static MechanicPayload condition(SourceFragment fragment) {
        if (fragment.kind() != SourceFragment.Kind.STRUCTURED_FIELD
                || !fragment.pointer().contains("conditionInflict")) return null;
        try {
            ConditionName condition = ConditionName.valueOf(fragment.text().toUpperCase(Locale.ROOT));
            return new ConditionChangeMechanic(ConditionOperation.APPLY, condition, java.util.List.of());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static DiceAmount dice(Matcher matcher) {
        int modifier = signedModifier(matcher);
        return new DiceAmount(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), modifier);
    }

    private static int signedModifier(Matcher matcher) {
        if (matcher.group(4) == null) return 0;
        int value = Integer.parseInt(matcher.group(4));
        return "-".equals(matcher.group(3)) ? -value : value;
    }

    private static SubjectReference target(String text, int effectOffset) {
        String prefix = text.substring(0, Math.min(effectOffset, text.length()));
        String lowerPrefix = prefix.toLowerCase(Locale.ROOT);
        String context = lowerPrefix.substring(Math.max(0, lowerPrefix.lastIndexOf('.') + 1));
        if (context.matches("(?s).*\\b(each|every) (creature|target)\\b.*")) {
            TargetScope scope = context.matches("(?s).*(within|in)\\b.*")
                    ? TargetScope.AREA_AFFECTED_CREATURES : TargetScope.MULTIPLE_TARGET_CREATURES;
            return new SpellTargetSubject(scope);
        }
        if (context.matches("(?s).*\\b(a|the) (target|creature|humanoid)\\b.*")
                || context.matches("(?s).*\\btarget\\b.*")) {
            return new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE);
        }
        return new UnknownSubject(UnknownValueReason.NOT_INTERPRETED);
    }
}
