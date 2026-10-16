package com.thedndpub.data.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dte.classes.ClassEquipmentEntryDte;
import com.thedndpub.data.dte.classes.ClassEquipmentGroupDte;
import com.thedndpub.data.dte.classes.ClassFeatureUnlockDte;
import com.thedndpub.data.dte.classes.ClassLevelProgressionDte;
import com.thedndpub.data.dte.classes.ClassOptionsEntryDte;
import com.thedndpub.data.dte.classes.ClassProficiencyDte;
import com.thedndpub.data.dte.classes.ClassSpellChoiceDte;
import com.thedndpub.data.dte.classes.ClassSpellLevelDte;
import com.thedndpub.data.dte.classes.ClassSpellSelectionDte;
import com.thedndpub.data.dte.classes.ClassStatblockEntryDte;
import com.thedndpub.data.dte.classes.ClassTableCellDte;
import com.thedndpub.data.dte.classes.ClassTableEntryDte;
import com.thedndpub.data.dte.classes.ClassTextEntryDte;

import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Map;

/** Independent test projection that reconstructs the observed source grammar from typed values. */
final class ClassSourceRoundTrip {
    private final ObjectMapper mapper = new ObjectMapper();

    JsonNode sourceTree(Object value) throws ReflectiveOperationException {
        if (value == null) {
            return mapper.nullNode();
        }
        if (value instanceof ClassTextEntryDte text) {
            return mapper.getNodeFactory().textNode(text.text());
        }
        if (value instanceof ClassProficiencyDte proficiency && proficiency.stringForm()) {
            return mapper.getNodeFactory().textNode(proficiency.proficiency());
        }
        if (value instanceof ClassFeatureUnlockDte unlock && unlock.stringForm()) {
            return mapper.getNodeFactory().textNode(unlock.classFeature());
        }
        if (value instanceof ClassEquipmentEntryDte equipment && equipment.stringForm()) {
            return mapper.getNodeFactory().textNode(equipment.item());
        }
        if (value instanceof ClassEquipmentGroupDte group) {
            return sourceTree(group.branches());
        }
        if (value instanceof ClassLevelProgressionDte progression) {
            return sourceTree(progression.dense() == null ? progression.milestones() : progression.dense());
        }
        if (value instanceof ClassTableCellDte cell) {
            if (cell.text() != null) {
                return sourceTree(cell.text());
            }
            if (cell.number() != null) {
                return sourceTree(cell.number());
            }
        }
        if (value instanceof ClassSpellLevelDte level && level.spells() != null) {
            return sourceTree(level.spells());
        }
        if (value instanceof ClassSpellSelectionDte spell && spell.spell() != null) {
            return sourceTree(spell.spell());
        }
        if (value instanceof ClassSpellChoiceDte choice && choice.filter() != null) {
            return sourceTree(choice.filter());
        }
        if (value instanceof Map<?, ?> values) {
            ObjectNode object = mapper.createObjectNode();
            for (var entry : values.entrySet()) {
                object.set(entry.getKey().toString(), sourceTree(entry.getValue()));
            }
            return object;
        }
        if (value instanceof List<?> values) {
            var array = mapper.createArrayNode();
            for (Object child : values) {
                array.add(sourceTree(child));
            }
            return array;
        }
        if (value.getClass().isRecord()) {
            ObjectNode object = mapper.createObjectNode();
            if (value instanceof ClassOptionsEntryDte) {
                object.put("type", "options");
            } else if (value instanceof ClassStatblockEntryDte) {
                object.put("type", "statblock");
            } else if (value instanceof ClassTableEntryDte) {
                object.put("type", "table");
            }
            for (RecordComponent component : value.getClass().getRecordComponents()) {
                if (component.getName().equals("stringForm")) {
                    continue;
                }
                Object fieldValue = component.getAccessor().invoke(value);
                if (fieldValue != null) {
                    JsonProperty property = component.getAccessor().getAnnotation(JsonProperty.class);
                    String name = property == null ? component.getName() : property.value();
                    object.set(name, sourceTree(fieldValue));
                }
            }
            return object;
        }
        return mapper.valueToTree(value);
    }
}
