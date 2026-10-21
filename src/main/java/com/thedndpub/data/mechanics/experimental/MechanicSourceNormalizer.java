package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.spell.SpellMechanicsSourceOwner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MechanicSourceNormalizer {
    private static final List<String> STRUCTURED = List.of(
            "spellAttack", "savingThrow", "damageInflict", "conditionInflict", "conditionImmune",
            "damageResist", "damageVulnerable", "abilityCheck", "areaTags", "miscTags", "scalingLevelDice");
    private static final List<String> CONTEXT_FIELDS = List.of("level", "school", "time", "range", "components", "duration");
    private final ObjectMapper mapper;

    public MechanicSourceNormalizer(ObjectMapper mapper) {
        this.mapper = ModelChecks.required(mapper, "mapper");
    }

    public List<SourceFragment> normalize(SpellDefinition spell, SpellMechanicsSourceOwner owner, byte[] sourceBytes) {
        try {
            return normalize(spell, owner, mapper.readTree(sourceBytes));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to normalize pinned spell resource", exception);
        }
    }

    public List<SourceFragment> normalize(SpellDefinition spell, SpellMechanicsSourceOwner owner, JsonNode resourceRoot) {
        ModelChecks.require(spell.id().equals(owner.id()), "Source fragment owner mismatch");
        JsonNode root = resourceRoot.at(owner.jsonPointer());
        ModelChecks.require(root.isObject(), "Missing spell source object");
        List<SourceFragment> result = new ArrayList<>();
        for (String field : STRUCTURED) {
            JsonNode value = root.path(field);
            if (!value.isMissingNode() && !value.isNull()) {
                if (field.equals("scalingLevelDice")) {
                    result.add(new SourceFragment(owner.id().value() + "#/" + field, "/" + field,
                            SourceFragment.Kind.STRUCTURED_FIELD, value.toString(), List.of(field),
                            List.of(MechanicType.SCALING), owner.revision()));
                } else {
                    addStructured(result, owner, field, value, "/" + field);
                }
            }
        }
        for (String field : CONTEXT_FIELDS) {
            JsonNode value = root.path(field);
            if (!value.isMissingNode() && !value.isNull()) {
                addStructured(result, owner, field, value, "/" + field);
            }
        }
        boolean structuredCharacterScaling = root.path("scalingLevelDice").isObject();
        walkEntries(result, owner, root.path("entries"), "/entries", List.of("entries"), structuredCharacterScaling);
        walkEntries(result, owner, root.path("entriesHigherLevel"), "/entriesHigherLevel",
                List.of("entriesHigherLevel"), structuredCharacterScaling);
        return result.stream().sorted(java.util.Comparator.comparing(SourceFragment::pointer)
                .thenComparing(fragment -> fragment.kind().name())).toList();
    }

    private static void addStructured(List<SourceFragment> output, SpellMechanicsSourceOwner owner,
                                      String field, JsonNode node, String pointer) {
        if (node.isArray()) {
            for (int index = 0; index < node.size(); index++) {
                addStructured(output, owner, field, node.get(index), pointer + "/" + index);
            }
            return;
        }
        String value = node.isValueNode() ? node.asText() : node.toString();
        List<MechanicType> types = switch (field) {
            case "spellAttack" -> List.of(MechanicType.ATTACK);
            case "savingThrow" -> List.of(MechanicType.SAVING_THROW);
            case "damageInflict", "damageResist", "damageVulnerable" -> List.of(MechanicType.DAMAGE);
            case "conditionInflict", "conditionImmune" -> List.of(MechanicType.CONDITION);
            case "areaTags", "miscTags" -> List.of();
            case "abilityCheck" -> List.of();
            default -> List.of();
        };
        output.add(new SourceFragment(owner.id().value() + "#" + pointer, pointer,
                SourceFragment.Kind.STRUCTURED_FIELD, value, List.of(field), types, owner.revision()));
    }

    private static void walkEntries(List<SourceFragment> output, SpellMechanicsSourceOwner owner,
                                    JsonNode node, String pointer, List<String> hierarchy,
                                    boolean structuredCharacterScaling) {
        if (node.isTextual()) {
            String text = node.textValue();
            output.add(new SourceFragment(owner.id().value() + "#" + pointer, pointer,
                    SourceFragment.Kind.AUTHORED_TEXT, text, hierarchy,
                    classifyText(text, structuredCharacterScaling), owner.revision()));
            return;
        }
        if (node.isArray()) {
            for (int index = 0; index < node.size(); index++) {
                walkEntries(output, owner, node.get(index), pointer + "/" + index, hierarchy,
                        structuredCharacterScaling);
            }
            return;
        }
        if (node.isObject()) {
            String title = node.path("name").isTextual() ? node.path("name").textValue() : null;
            List<String> childHierarchy = title == null ? hierarchy : append(hierarchy, title);
            node.fields().forEachRemaining(entry -> {
                if (entry.getKey().equals("entries") || entry.getKey().equals("items")
                        || entry.getKey().equals("entry") || entry.getKey().equals("name")) {
                    walkEntries(output, owner, entry.getValue(), pointer + "/" + escape(entry.getKey()),
                            childHierarchy, structuredCharacterScaling);
                }
            });
        }
    }

    private static List<MechanicType> classifyText(String text, boolean structuredCharacterScaling) {
        String lower = text.toLowerCase(java.util.Locale.ROOT);
        List<MechanicType> result = new ArrayList<>();
        if (lower.contains("spell attack") || lower.contains("attack roll")) result.add(MechanicType.ATTACK);
        if (lower.contains("damage") || lower.matches("(?s).*\\{@damage\\s+[^}]+}.*")) result.add(MechanicType.DAMAGE);
        if (lower.contains("saving throw") || lower.contains("save dc")) result.add(MechanicType.SAVING_THROW);
        if (lower.contains("condition") || lower.matches("(?s).*\\b(blinded|charmed|deafened|exhaustion|frightened|grappled|incapacitated|invisible|paralyzed|petrified|poisoned|prone|restrained|stunned|unconscious)\\b.*")) {
            result.add(MechanicType.CONDITION);
        }
        boolean conditionDuration = (lower.contains("condition") || lower.matches("(?s).*\\b(blinded|charmed|"
                + "deafened|exhaustion|frightened|grappled|incapacitated|invisible|paralyzed|petrified|poisoned|"
                + "prone|restrained|stunned|unconscious)\\b.*"))
                && lower.matches("(?s).*(?:for the duration|until the spell ends|"
                + "for\\s+(?:up to\\s+)?\\d+\\s+(?:round|minute|hour|day)s?|until the end of .+ turn).*" );
        if (conditionDuration) result.add(MechanicType.DURATION);
        boolean conditionTerminationEvent = lower.matches("(?is).*\\b(?:can use|uses) (?:its|their|the target's) action\\b"
                + ".*\\b(?:check|attempt|escape|free itself|free the target)\\b.*"
                + "\\b(?:on a success|if it succeeds|if successful|on success)\\b.*")
                && (lower.matches("(?is).*\\b(?:free|frees|escape|escapes|end|ends|remove|removes)\\b.*")
                        || lower.matches("(?is).*\\bis no longer\\b.*"))
                && (lower.contains("{@condition") || lower.contains("condition"));
        if (conditionTerminationEvent) result.add(MechanicType.CONDITION_TERMINATION);
        if (lower.contains("regain") && lower.contains("hit point") || lower.contains("heals")) result.add(MechanicType.HEALING);
        boolean slotScaling = lower.contains("using this spell's slot")
                || lower.contains("using a spell slot") || lower.contains("for each slot level")
                || lower.contains("for each spell slot level")
                || lower.contains("at higher levels") && !lower.strip().equals("at higher levels");
        boolean characterScaling = !structuredCharacterScaling && (lower.contains("when you reach")
                || lower.matches("(?s).*\\b(?:damage|healing) increases by .*\\b(?:character )?level\\b.*")
                || lower.matches("(?s).*\\b(?:beam|ray|dart|missile|target)s? at (?:level )?\\d+(?:st|nd|rd|th)? level.*")
                || lower.matches("(?s).*\\b(?:beam|ray|dart|missile|target)s? at level \\d+.*"));
        if (slotScaling || characterScaling) {
            result.add(MechanicType.SCALING);
        }
        return result;
    }

    private static List<String> append(List<String> source, String value) {
        List<String> result = new ArrayList<>(source);
        result.add(value);
        return List.copyOf(result);
    }

    private static String escape(String token) { return token.replace("~", "~0").replace("/", "~1"); }
}
