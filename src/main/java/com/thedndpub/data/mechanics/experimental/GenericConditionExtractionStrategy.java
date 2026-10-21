package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionChoiceMechanic;
import com.thedndpub.data.mechanics.model.ConditionName;
import com.thedndpub.data.mechanics.model.ConditionOperation;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.SubjectReference;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Extracts explicit condition operations; condition tags alone never establish an operation. */
public final class GenericConditionExtractionStrategy implements MechanicExtractionStrategy {
    private static final Pattern CONDITION = Pattern.compile("(?i)\\b(" + String.join("|",
            java.util.Arrays.stream(ConditionName.values()).map(value -> value.name().toLowerCase(Locale.ROOT))
                    .sorted(Comparator.comparingInt(String::length).reversed()).toList()) + ")\\b");

    @Override public String strategyId() { return "generic-condition-operations"; }
    @Override public String version() { return "1"; }
    @Override public Set<MechanicType> supportedTypes() { return Set.of(MechanicType.CONDITION); }

    @Override
    public StrategyExtraction extract(MechanicCandidate candidate, SourceFragment fragment, SpellDefinition spell) {
        if (fragment.kind() != SourceFragment.Kind.AUTHORED_TEXT) {
            return StrategyExtraction.unsupported("CONDITION_TAG_SEMANTICS_UNRESOLVED",
                    "Structured condition tags identify a condition but do not establish apply, remove, immunity, or prevention semantics.");
        }

        List<ConditionMention> conditions = conditionMentions(fragment.text());
        if (conditions.isEmpty()) {
            return StrategyExtraction.unsupported("CONDITION_NAME_UNRESOLVED",
                    "The authored fragment signals a condition mechanic but no supported condition name was identified.");
        }

        if (hasExplicitChoice(fragment.text())) {
            ConditionOperation operation = choiceOperation(fragment.text(), conditions.getFirst());
            if (operation == null || conditions.stream().map(ConditionMention::condition).distinct().count() < 2) {
                return StrategyExtraction.unsupported("CONDITION_CHOICE_UNRESOLVED",
                        "The source signals a condition choice, but its operation or alternatives are not unambiguous.");
            }
            if (mentionsUnsupportedChoiceCount(fragment.text())) {
                return StrategyExtraction.unsupported("CONDITION_CHOICE_COUNT_UNSUPPORTED",
                        "The source choice selects a count other than one, which this typed choice model does not represent.");
            }
            List<ConditionName> alternatives = conditions.stream().map(ConditionMention::condition).distinct().toList();
            List<ExtractedMechanicPayload> payloads = new ArrayList<>();
            payloads.add(new ExtractedMechanicPayload(new ConditionChoiceMechanic(operation, alternatives, 1),
                    context(fragment.text(), conditions.getFirst().start())));
            return new StrategyExtraction(payloads, null, null);
        }

        List<ExtractedMechanicPayload> payloads = new ArrayList<>();
        for (ConditionMention mention : conditions) {
            ConditionOperation matched = operationForMention(fragment.text(), mention);
            if (matched == null) continue;
            payloads.add(new ExtractedMechanicPayload(new ConditionChangeMechanic(matched,
                    mention.condition(), List.of()), context(fragment.text(), mention.start()),
                    mention.start(), mention.end()));
        }
        if (payloads.isEmpty()) {
            return StrategyExtraction.unsupported("CONDITION_OPERATION_UNRESOLVED",
                    "A condition is mentioned, but the authored text does not establish its application, removal, immunity, or prevention.");
        }
        return new StrategyExtraction(payloads, null, null);
    }

    private static List<ConditionMention> conditionMentions(String text) {
        Matcher matcher = CONDITION.matcher(text);
        List<ConditionMention> result = new ArrayList<>();
        while (matcher.find()) {
            try {
                result.add(new ConditionMention(ConditionName.valueOf(matcher.group(1).toUpperCase(Locale.ROOT)),
                        matcher.start(), matcher.end()));
            } catch (IllegalArgumentException ignored) {
                // The enum is the supported vocabulary; unknown terms remain unrecognized.
            }
        }
        return result;
    }

    private static ConditionOperation operationForMention(String text, ConditionMention mention) {
        String prefix = text.substring(Math.max(0, mention.start() - 100), mention.start())
                .replaceAll("(?i)\\{@condition\\s+", "")
                .replace("}", "")
                .toLowerCase(Locale.ROOT);
        String local = prefix.substring(Math.max(0, prefix.lastIndexOf('.') + 1));
        if (Pattern.compile("\\b(?:on|after) (?:a )?successful (?:(?:strength|dexterity|constitution|intelligence|"
                + "wisdom|charisma) )?(?:saving throw|save)\\b[^.;]{0,70}"
                + "\\b(?:is|becomes?) not\\s+(?:the\\s+)?$").matcher(local).find()) {
            return ConditionOperation.PREVENT;
        }
        if (Pattern.compile("\\bnot\\s+(?:be\\s+)?$").matcher(local).find()) return null;
        if (Pattern.compile("(?s).*\\b(?:can't|cannot) be\\b[^.;]{0,70}$").matcher(local).find()) {
            return ConditionOperation.PREVENT;
        }
        if (Pattern.compile("\\b(?:can't|cannot|doesn't|does not) gain\\b[^.;]{0,70}(?:the\\s+)?$")
                .matcher(local).find()) return ConditionOperation.PREVENT;
        if (Pattern.compile("\\bimmune to (?:being |the )?(?:the )?$").matcher(local).find()) {
            return ConditionOperation.GRANT_IMMUNITY;
        }
        if (Pattern.compile("\\bprevent(?:s|ed)?\\b[^.;]{0,70}\\bfrom being\\s+(?:the\\s+)?$")
                .matcher(local).find()) return ConditionOperation.PREVENT;
        if (explicitlyImposed(text, mention, local)) return ConditionOperation.APPLY;
        if (Pattern.compile("\\b(?:remove|removes|removed|end|ends|ended|cure|cures|cured)\\b"
                + "[^.;:]{0,70}(?:the\\s+)?$").matcher(local).find()) return ConditionOperation.REMOVE;
        if (Pattern.compile("\\b(?:becomes?|be|is|are|was|were|gains?|suffers? from)"
                + "(?:\\s+(?:the|also|then|either))?\\s+$").matcher(local).find()) return ConditionOperation.APPLY;
        return null;
    }

    private static boolean explicitlyImposed(String text, ConditionMention mention, String local) {
        boolean requiredOnFailure = Pattern.compile("(?i)\\bmust succeed on (?:a )?(?:strength|dexterity|constitution|"
                + "intelligence|wisdom|charisma) saving throw or have (?:the\\s+)?$").matcher(local).find();
        if (requiredOnFailure) return true;
        if (!Pattern.compile("\\b(?:has|have)\\s+(?:the\\s+)?$").matcher(local).find()) return false;
        String suffix = text.substring(mention.end(), Math.min(text.length(), mention.end() + 100));
        return Pattern.compile("(?is)^\\s+(?:condition\\s+)?(?:imposed|inflicted|applied)\\b")
                .matcher(suffix).find();
    }

    private static ConditionOperation choiceOperation(String text, ConditionMention first) {
        if (Pattern.compile("(?is).*\\b(?:remove|end|cure)\\b(?: either)?\\s+(?:one|a)\\s+"
                + "(?:disease|condition)\\b.*\\bcondition can be\\b").matcher(text).find()) {
            return ConditionOperation.REMOVE;
        }
        return operationForMention(text, first);
    }

    private static boolean hasExplicitChoice(String text) {
        return Pattern.compile("(?i)\\b(?:choose|select|pick)\\b[^.;]{0,100}"
                + "\\b(?:one of|either|one condition|a condition)\\b|"
                + "\\bone of the following conditions?\\b|\\byour choice\\b|"
                + "\\bcondition can be\\b[^.;]{0,100}\\bor\\b").matcher(text).find();
    }

    private static boolean mentionsUnsupportedChoiceCount(String text) {
        return Pattern.compile("(?i)\\bchoose (?:two|three|both|all|\\d+)\\b").matcher(text).find();
    }

    private static MechanicContext context(String text, int start) {
        String prefix = text.substring(0, Math.min(start, text.length())).toLowerCase(Locale.ROOT);
        String clause = prefix.substring(Math.max(0, prefix.lastIndexOf('.') + 1));
        SubjectReference subject;
        if (clause.matches("(?s).*\\b(?:each|every) (?:creature|target)\\b.*")) {
            subject = new SpellTargetSubject(TargetScope.MULTIPLE_TARGET_CREATURES);
        } else if (clause.matches("(?s).*\\b(?:a|the|one) (?:target|creature|humanoid)\\b.*")) {
            subject = new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE);
        } else {
            subject = new UnknownSubject(UnknownValueReason.NOT_INTERPRETED);
        }
        return new MechanicContext(subject, List.of());
    }

    private record ConditionMention(ConditionName condition, int start, int end) { }
}
