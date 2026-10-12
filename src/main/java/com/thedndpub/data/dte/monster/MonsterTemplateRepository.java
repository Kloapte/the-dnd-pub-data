package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Immutable, validated snapshot of template definitions, including metadata internal copies.
 */
public final class MonsterTemplateRepository {
    private final Map<MonsterIdentity, ObjectNode> templates;

    public MonsterTemplateRepository(ObjectMapper mapper, InputStream input) throws IOException {
        JsonNode root = mapper.readTree(input);
        JsonNode array = root.path("monsterTemplate");
        if (!array.isArray()) {
            throw new MonsterResolutionException("template.json $.monsterTemplate must be an array");
        }
        Map<MonsterIdentity, ObjectNode> raw = new TreeMap<>();
        for (int i = 0; i < array.size(); i++) {
            JsonNode n = array.get(i);
            if (!n.isObject() || !n.path("name").isTextual() || !n.path("source").isTextual()) {
                throw new MonsterResolutionException("template.json $.monsterTemplate[" + i + "] requires name/source");
            }
            validateFields(n, Set.of("name", "source", "page", "ref", "alias", "prerequisite", "crMin", "apply", "_copy"), "$.monsterTemplate[" + i + "]");
            JsonNode apply = n.get("apply");
            if (apply != null && !apply.isNull()) {
                if (!apply.isObject()) {
                    throw new MonsterResolutionException("template.json $.monsterTemplate[" + i + "].apply must be an object");
                }
                validateFields(apply, Set.of("_root", "_mod"), "$.monsterTemplate[" + i + "].apply");
            }
            MonsterIdentity id = MonsterIdentity.from(n.path("name").asText(), n.path("source").asText());
            if (raw.putIfAbsent(id, (ObjectNode) n.deepCopy()) != null) {
                throw new MonsterResolutionException("Ambiguous template identity " + id);
            }
        }
        Map<MonsterIdentity, ObjectNode> resolved = new TreeMap<>();
        Set<MonsterIdentity> visiting = new HashSet<>();
        for (MonsterIdentity id : raw.keySet()) {
            resolveTemplate(id, raw, resolved, visiting);
        }
        templates = Collections.unmodifiableMap(resolved);
    }

    public static MonsterTemplateRepository fromResource(ObjectMapper mapper) {
        InputStream in = MonsterTemplateRepository.class.getResourceAsStream("/data/monsters/template.json");
        if (in == null) {
            throw new MonsterResolutionException("Missing resource /data/monsters/template.json");
        }
        try (in) {
            return new MonsterTemplateRepository(mapper, in);
        } catch (IOException e) {
            throw new MonsterResolutionException("Cannot read template asset", e);
        }
    }

    public ObjectNode require(MonsterIdentity id) {
        ObjectNode n = templates.get(id);
        if (n == null) {
            throw new MonsterResolutionException("Missing template " + id);
        }
        return n.deepCopy();
    }

    public Set<MonsterIdentity> identities() {
        return templates.keySet();
    }

    private static void validateFields(JsonNode n, Set<String> allowed, String path) {
        n.fieldNames().forEachRemaining(f -> {
            if (!allowed.contains(f)) {
                throw new MonsterResolutionException("template.json " + path + "." + f + " is unsupported");
            }
        });
    }

    private static ObjectNode resolveTemplate(MonsterIdentity id, Map<MonsterIdentity, ObjectNode> raw, Map<MonsterIdentity, ObjectNode> out, Set<MonsterIdentity> visiting) {
        if (out.containsKey(id)) {
            return out.get(id).deepCopy();
        }
        if (!visiting.add(id)) {
            throw new MonsterResolutionException("Template inheritance cycle at " + id);
        }
        ObjectNode child = raw.get(id).deepCopy();
        JsonNode copy = child.get("_copy");
        if (copy != null && !copy.isNull()) {
            if (!copy.path("name").isTextual() || !copy.path("source").isTextual()) {
                throw new MonsterResolutionException("Malformed template _copy at " + id);
            }
            MonsterIdentity parentId = MonsterIdentity.from(copy.path("name").asText(), copy.path("source").asText());
            ObjectNode parent = raw.containsKey(parentId) ? resolveTemplate(parentId, raw, out, visiting) : null;
            if (parent == null) {
                throw new MonsterResolutionException("Missing template parent " + parentId + " referenced by " + id);
            }
            ObjectNode apply = child.path("apply").isObject() ? (ObjectNode) child.get("apply") : com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode();
            JsonNode inherited = parent.path("apply");
            if (inherited.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> inheritedFields = inherited.fields();
                while (inheritedFields.hasNext()) {
                    var e = inheritedFields.next();
                    if (!apply.has(e.getKey())) {
                        apply.set(e.getKey(), e.getValue().deepCopy());
                    }
                }
            }
            JsonNode mod = copy.path("_mod");
            if (mod.isObject()) {
                applyModifierInstructions(child, mod);
                if (child.path("apply").isObject()) {
                    apply = (ObjectNode) child.get("apply");
                }
            }
            child.set("apply", apply);
            child.remove("_copy");
        }
        visiting.remove(id);
        out.put(id, child.deepCopy());
        return child;
    }

    private static ObjectNode applyModifierInstructions(ObjectNode template, JsonNode mods) {
        mods.fields().forEachRemaining(t -> {
            JsonNode inst = t.getValue();
            for (JsonNode op : asInstructions(inst)) {
                if (!"setProp".equals(op.path("mode").asText())) {
                    throw new MonsterResolutionException("Unsupported internal template modifier mode " + op.path("mode"));
                }
                String path = op.path("prop").asText();
                JsonNode value = op.get("value");
                if (path.isBlank() || value == null) {
                    throw new MonsterResolutionException("Malformed internal template setProp");
                }
                setPath(template, path, value.deepCopy());
            }
        });
        return template;
    }

    static List<JsonNode> asInstructions(JsonNode n) {
        if (n.isArray()) {
            List<JsonNode> a = new ArrayList<>();
            n.forEach(a::add);
            return a;
        }
        if (n.isObject() && n.has("mode")) {
            return List.of(n);
        }
        if (n.isObject()) {
            List<JsonNode> a = new ArrayList<>();
            n.fields().forEachRemaining(e -> a.add(e.getValue()));
            return a;
        }
        throw new MonsterResolutionException("Modifier instruction must be an object or array");
    }

    static void setPath(ObjectNode root, String path, JsonNode value) {
        String[] parts = path.split("\\.");
        ObjectNode at = root;
        for (int i = 0; i < parts.length - 1; i++) {
            JsonNode n = at.get(parts[i]);
            if (!(n instanceof ObjectNode)) {
                n = at.putObject(parts[i]);
            }
            at = (ObjectNode) n;
        }
        at.set(parts[parts.length - 1], value);
    }
}
