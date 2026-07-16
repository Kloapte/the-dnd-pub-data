package com.thedndpub.data.dte.items.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.items.ItemAttachedSpellDte;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AttachedSpellDeserializer extends ValueDeserializer<List<ItemAttachedSpellDte>> {
    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "charges",
            "limited",
            "daily",
            "rest",
            "will",
            "ritual",
            "other",
            "ability"
    );

    @Override
    public List<ItemAttachedSpellDte> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        List<ItemAttachedSpellDte> attachedSpells = new ArrayList<>();
        if (node.isArray()) {
            for(JsonNode subNode : node) {
                if(subNode.isTextual()) {
                    ItemAttachedSpellDte dte = new ItemAttachedSpellDte();
                    dte.setName(subNode.asText());
                }
                else {
                    throw new RuntimeException("Not text as attached spell list");
                }
            }
        }
        else {
            String ability = this.getAbility(node);
            if(node.hasNonNull("charges")) {
                JsonNode chargesNode = node.get("charges");
                for (var chargeEntry : chargesNode.properties()) {
                    String key = chargeEntry.getKey();
                    int charges = Integer.parseInt(key);

                    JsonNode spellsNode = chargeEntry.getValue();

                    for (JsonNode chargeSubNode : spellsNode) {
                        ItemAttachedSpellDte spell = makeAttachedSpell(chargeSubNode, ability);
                        spell.setCharges(charges);
                        attachedSpells.add(spell);
                    }
                }
            }
            if(node.hasNonNull("limited")) {
                JsonNode limitedNode = node.get("limited");

                for (var limitedEntry : limitedNode.properties()) {
                    int charges = Integer.parseInt(limitedEntry.getKey());

                    for (JsonNode chargeSubNode : limitedEntry.getValue()) {
                        ItemAttachedSpellDte spell = makeAttachedSpell(chargeSubNode, ability);
                        spell.setLimitedUses(charges);
                        attachedSpells.add(spell);
                    }
                }
            }
            if(node.hasNonNull("daily")) {
                JsonNode dailyNode = node.get("daily");
                if(dailyNode.get("1") != null) {
                    for(JsonNode dailySubNode : dailyNode.get("1")) {
                        ItemAttachedSpellDte spell = makeAttachedSpell(dailySubNode, ability);
                        spell.setDaily(true);
                        attachedSpells.add(spell);
                    }
                }

                if(dailyNode.get("1e") != null) {
                    for(JsonNode dailySubNode : dailyNode.get("1e")) {
                        ItemAttachedSpellDte spell = makeAttachedSpell(dailySubNode, ability);
                        spell.setDaily(true);
                        attachedSpells.add(spell);
                    }
                }
            }
            if(node.hasNonNull("rest")) {
                JsonNode restNode = node.get("rest");
                if(restNode.get("1e") != null) {
                    for(JsonNode restSubNode : restNode.get("1e")) {
                        ItemAttachedSpellDte spell = makeAttachedSpell(restSubNode, ability);
                        spell.setRest(true);
                        attachedSpells.add(spell);
                    }
                }
            }
            if(node.hasNonNull("will")) {
                for(JsonNode willNode : node.get("will")) {
                    ItemAttachedSpellDte spell = makeAttachedSpell(willNode, ability);
                    spell.setAtWill(true);
                    attachedSpells.add(spell);
                }
            }
            if(node.hasNonNull("ritual")) {
                for(JsonNode ritualNode : node.get("ritual")) {
                    ItemAttachedSpellDte spell = makeAttachedSpell(ritualNode, ability);
                    spell.setRitual(true);
                    attachedSpells.add(spell);
                }
            }
            if(node.hasNonNull("other")) {
                for(JsonNode otherNode : node.get("other")) {
                    ItemAttachedSpellDte spell = makeAttachedSpell(otherNode, ability);
                    spell.setOther(true);
                    attachedSpells.add(spell);
                }
            }

            //Checking if more fields available
            for (var entry : node.properties()) {
                String fieldName = entry.getKey();

                if (!ALLOWED_FIELDS.contains(fieldName)) {
                    throw new RuntimeException(
                            "Unknown attached spell property: " + fieldName
                    );
                }
            }
        }

        return attachedSpells;
    }

    private ItemAttachedSpellDte makeAttachedSpell(JsonNode node, String ability) {
        ItemAttachedSpellDte spell = new ItemAttachedSpellDte();
        spell.setAbility(ability);
        spell.setName(node.asText());
        return spell;
    }

    private String getAbility(JsonNode node) {
        if(node.get("ability") != null) {
            return node.get("ability").asText();
        }

        return null;
    }
}