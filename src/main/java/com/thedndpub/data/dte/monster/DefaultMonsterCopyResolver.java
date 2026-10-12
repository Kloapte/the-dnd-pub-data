package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import java.util.*;
import java.util.regex.*;

/**
 * Copy/template resolver compatible with the pinned 5etools copy pipeline.
 */
public final class DefaultMonsterCopyResolver implements MonsterCopyResolver {
    private static final Set<String> PRESERVE = Set.of("page", "otherSources", "referenceSources", "srd", "srd52", "basicRules", "basicRules2024", "reprintedAs", "hasFluff", "hasFluffImages", "hasToken", "tokenCredit", "tokenCustom", "foundryTokenScale", "altArt", "_versions");
    private static final Set<String> ENTRY_TARGETS = Set.of("action", "bonus", "reaction", "trait", "legendary", "mythic", "variant", "spellcasting", "actionHeader", "bonusHeader", "reactionHeader", "traitHeader", "legendaryHeader", "mythicHeader");
    private final ObjectMapper mapper;
    private final MonsterTemplateRepository templates;

    public DefaultMonsterCopyResolver(ObjectMapper mapper, MonsterTemplateRepository templates) {
        this.mapper = Objects.requireNonNull(mapper);
        this.templates = Objects.requireNonNull(templates);
    }

    @Override
    public List<ResolvedMonster> resolveAll(List<MonsterSourceRecord> monsters) {
        MonsterCopyGraph graph = new MonsterCopyGraph(monsters);
        Map<MonsterIdentity, ObjectNode> cache = new HashMap<>();
        Map<MonsterIdentity, Set<String>> modeCache = new HashMap<>();
        List<ResolvedMonster> result = new ArrayList<>();
        for (var r : monsters) {
            ObjectNode n = resolve(r.identity(), graph, cache, modeCache);
            var cp = r.dte().copy();
            List<MonsterIdentity> tids = cp == null || cp.templates() == null ? List.of() : cp.templates().stream().map(t -> MonsterIdentity.from(t.name(), t.source())).toList();
            result.add(new ResolvedMonster(r.identity(), r.location(), cp == null ? null : MonsterIdentity.from(cp.name(), cp.source()), tids, n, new ResolutionProvenance("1", "8c026b807fac21862a309379a0c3228a0683198b", cp == null ? null : cp.name(), cp == null ? null : cp.source(), n.has("_versions"), modeCache.getOrDefault(r.identity(), Set.of()))));
        }
        return List.copyOf(result);
    }

    private ObjectNode resolve(MonsterIdentity id, MonsterCopyGraph g, Map<MonsterIdentity, ObjectNode> cache, Map<MonsterIdentity, Set<String>> modeCache) {
        ObjectNode hit = cache.get(id);
        if (hit != null) {
            return hit.deepCopy();
        }
        MonsterSourceRecord r = g.records().get(id);
        ObjectNode child = r.original();
        MonsterIdentity parentId = g.parents().get(id);
        if (parentId == null) {
            child.remove("_copy");
            cache.put(id, child.deepCopy());
            modeCache.put(id, Set.of());
            return child;
        }
        ObjectNode parent = resolve(parentId, g, cache, modeCache);
        Set<String> applied = new TreeSet<>(modeCache.getOrDefault(parentId, Set.of()));
        Set<String> originalKeys = new HashSet<>();
        child.fieldNames().forEachRemaining(originalKeys::add);
        JsonNode copy = child.path("_copy");
        JsonNode preserve = copy.path("_preserve");
        parent.fields().forEachRemaining(e -> {
            String k = e.getKey();
            if (k.equals("_copy")) {
                return;
            }
            if (originalKeys.contains(k)) {
                if (child.get(k).isNull()) {
                    child.remove(k);
                }
                return;
            }
            if (PRESERVE.contains(k) && !preserve.path(k).asBoolean(false) && !preserve.path("*").asBoolean(false)) {
                return;
            }
            child.set(k, e.getValue().deepCopy());
        });
        List<MonsterIdentity> templateIds = new ArrayList<>();
        JsonNode templateRefs = copy.path("_templates");
        List<ObjectNode> templateDefs = new ArrayList<>();
        if (templateRefs.isArray()) {
            for (JsonNode tr : templateRefs) {
                if (!tr.path("name").isTextual() || !tr.path("source").isTextual()) {
                    throw error(r, "_copy._templates", "template requires name/source");
                }
                MonsterIdentity tid = MonsterIdentity.from(tr.path("name").asText(), tr.path("source").asText());
                templateIds.add(tid);
                try {
                    templateDefs.add(templates.require(tid));
                } catch (MonsterResolutionException e) {
                    throw error(r, "_copy._templates", "cannot resolve " + tid + ": " + e.getMessage(), e);
                }
            }
        }
        for (ObjectNode t : templateDefs) {
            JsonNode root = t.path("apply").path("_root");
            if (root.isObject()) {
                root.fields().forEachRemaining(e -> {
                    if (!originalKeys.contains(e.getKey())) {
                        child.set(e.getKey(), e.getValue().deepCopy());
                    }
                });
            }
        }
        ObjectNode allMods = combineMods(copy.path("_mod"), templateDefs, r);
        applied.addAll(applyAll(child, allMods, r));
        child.remove("_copy");
        cache.put(id, child.deepCopy());
        modeCache.put(id, Set.copyOf(applied));
        return child;
    }

    private ObjectNode combineMods(JsonNode child, List<ObjectNode> defs, MonsterSourceRecord r) {
        ObjectNode all = child.isObject() ? (ObjectNode) child.deepCopy() : mapper.createObjectNode();
        for (ObjectNode t : defs) {
            JsonNode mods = t.path("apply").path("_mod");
            if (!mods.isObject()) {
                continue;
            }
            mods.fields().forEachRemaining(e -> {
                JsonNode old = all.get(e.getKey());
                ArrayNode dest = old == null ? mapper.createArrayNode() : instructions(old);
                dest.addAll(instructions(e.getValue()));
                all.set(e.getKey(), dest);
            });
        }
        return all;
    }

    private ArrayNode instructions(JsonNode n) {
        ArrayNode a = mapper.createArrayNode();
        if (n.isArray()) {
            n.forEach(x -> a.add(x.deepCopy()));
        } else if (n.isObject() && n.has("mode")) {
            a.add(n.deepCopy());
        } else if (n.isObject()) {
            n.fields().forEachRemaining(e -> {
                if (e.getValue().isArray()) {
                    e.getValue().forEach(x -> a.add(x.deepCopy()));
                } else {
                    a.add(e.getValue().deepCopy());
                }
            });
        } else if (n.isTextual() && n.asText().equals("remove")) {
            a.addObject().put("mode", "remove");
        } else {
            throw new MonsterResolutionException("Malformed modifier target value: " + n);
        }
        return a;
    }

    private Set<String> applyAll(ObjectNode root, ObjectNode mods, MonsterSourceRecord r) {
        Set<String> executed = new TreeSet<>();
        List<String> keys = new ArrayList<>();
        mods.fieldNames().forEachRemaining(keys::add);
        keys.sort(Comparator.<String>comparingInt(k -> k.equals("_") ? 1 : k.equals("*") ? 2 : 0).thenComparing(k -> k));
        for (String target : keys) {
            for (JsonNode op : instructions(mods.get(target))) {
                if (!op.path("mode").isTextual()) {
                    throw error(r, "_copy._mod." + target, "modifier has no mode");
                }
                String mode = op.path("mode").asText();
                Set<String> known = Set.of("setProp", "remove", "appendArr", "prependArr", "appendIfNotExistsArr", "insertArr", "replaceArr", "removeArr", "renameArr", "replaceTxt", "addSenses", "maxSize", "prefixSuffixStringProp", "scalarAddProp", "scalarMultProp", "scalarMultXp", "scalarAddHit", "scalarAddDc", "addSkills", "addSpells", "replaceSpells", "removeSpells");
                if (!known.contains(mode)) {
                    throw error(r, "_copy._mod." + target, "unsupported modifier mode '" + mode + "'");
                }
                String actualTarget = target;
                List<JsonNode> nodes;
                if (target.equals("*")) {
                    nodes = new ArrayList<>();
                    for (String k : ENTRY_TARGETS) {
                        JsonNode x = root.get(k);
                        if (x != null) {
                            nodes.add(x);
                        }
                    }
                } else {
                    JsonNode n = target.equals("_") ? root : atPath(root, target);
                    nodes = new ArrayList<>();
                    if (n != null) {
                        nodes.add(n);
                    } else if (Set.of("appendArr", "prependArr", "appendIfNotExistsArr").contains(mode)) {
                        nodes.add(root);
                    } else if (!Set.of("scalarAddProp", "scalarMultProp", "prefixSuffixStringProp", "scalarAddHit", "scalarAddDc").contains(mode)) {
                        throw error(r, "_copy._mod." + target, "target property is missing for modifier " + mode);
                    }
                }
                try {
                    for (JsonNode n : nodes) {
                        apply(mode, root, n, actualTarget, op, r);
                    }
                    executed.add(mode);
                } catch (RuntimeException e) {
                    if (e instanceof MonsterResolutionException) {
                        throw e;
                    }
                    throw error(r, "_copy._mod." + target + "/" + mode, e.getMessage(), e);
                }
            }
        }
        return Set.copyOf(executed);
    }

    private void apply(String mode, ObjectNode root, JsonNode target, String path, JsonNode op, MonsterSourceRecord r) {
        validateInstruction(mode, op, r, path);
        switch (mode) {
            case "setProp" -> {
                String p = reqText(op, "prop", r, path, mode);
                JsonNode v = op.get("value");
                if (v == null) {
                    throw error(r, path, mode + " requires value");
                }
                MonsterTemplateRepository.setPath(root, p, v.deepCopy());
            }
            case "remove" -> {
                String p = op.path("prop").asText(path);
                removePath(root, p);
            }
            case "appendArr", "prependArr", "appendIfNotExistsArr", "insertArr", "replaceArr", "removeArr",
                 "renameArr" -> arrayMode(mode, root, path, op, r);
            case "replaceTxt" -> textMode(target, op, r, path);
            case "addSenses" -> senses(root, op);
            case "maxSize" -> maxSize(root, op);
            case "prefixSuffixStringProp" -> stringWrap(root, path, op);
            case "scalarAddProp", "scalarMultProp" -> scalarProps(root, path, op, mode);
            case "scalarMultXp" -> scalarXp(root, op);
            case "scalarAddHit", "scalarAddDc" -> scalarTags(target, op, mode);
            case "addSkills" -> addSkills(root, op);
            case "addSpells", "replaceSpells", "removeSpells" -> spellMode(root, op, mode);
            default -> throw error(r, "_copy._mod." + path, "unsupported modifier mode '" + mode + "'");
        }
    }

    private void validateInstruction(String mode, JsonNode op, MonsterSourceRecord r, String target) {
        Set<String> common = Set.of("mode");
        Set<String> allowed = switch (mode) {
            case "setProp" -> union(common, "prop", "value");
            case "remove" -> union(common, "prop");
            case "appendArr", "prependArr", "appendIfNotExistsArr" -> union(common, "items");
            case "insertArr" -> union(common, "items", "index");
            case "replaceArr" -> union(common, "replace", "items");
            case "removeArr" -> union(common, "names", "items", "force");
            case "renameArr" -> union(common, "renames");
            case "replaceTxt" -> union(common, "replace", "with", "flags", "props", "tagInsensitive");
            case "addSenses" -> union(common, "senses");
            case "maxSize" -> union(common, "max");
            case "prefixSuffixStringProp" -> union(common, "prop", "prefix", "suffix");
            case "scalarAddProp", "scalarMultProp" -> union(common, "prop", "scalar", "floor");
            case "scalarMultXp", "scalarAddHit", "scalarAddDc" -> union(common, "scalar", "floor");
            case "addSkills" -> union(common, "skills");
            case "addSpells" ->
                    union(common, "name", "spells", "daily", "yearly", "will", "constant", "ritual", "rest", "restLong", "recharge", "legendary", "charges", "weekly", "monthly");
            case "replaceSpells" -> union(common, "spells", "daily");
            case "removeSpells" ->
                    union(common, "spells", "daily", "yearly", "will", "constant", "ritual", "rest", "restLong", "recharge", "legendary", "charges", "weekly", "monthly");
            default -> common;
        };
        if (!op.isObject()) {
            throw error(r, "_copy._mod." + target + "/" + mode, "instruction must be an object");
        }
        op.fieldNames().forEachRemaining(f -> {
            if (!allowed.contains(f)) {
                throw error(r, "_copy._mod." + target + "/" + mode, "unsupported property '" + f + "'");
            }
        });
    }

    private static Set<String> union(Set<String> base, String... names) {
        Set<String> out = new HashSet<>(base);
        out.addAll(List.of(names));
        return out;
    }

    private void arrayMode(String mode, ObjectNode root, String p, JsonNode o, MonsterSourceRecord r) {
        JsonNode n = atPath(root, p);
        ArrayNode a;
        if (n == null || n.isNull()) {
            if (Set.of("appendArr", "prependArr", "appendIfNotExistsArr").contains(mode)) {
                a = mapper.createArrayNode();
                MonsterTemplateRepository.setPath(root, p, a);
            } else {
                throw error(r, "_copy._mod." + p, mode + " target array missing");
            }
        } else if (n instanceof ArrayNode ar) {
            a = ar;
        } else {
            throw error(r, "_copy._mod." + p, mode + " target is not array");
        }
        ArrayNode items = payloadItems(o);
        switch (mode) {
            case "appendArr" -> a.addAll(items);
            case "prependArr" -> {
                for (int i = items.size() - 1; i >= 0; i--) {
                    a.insert(0, items.get(i));
                }
            }
            case "appendIfNotExistsArr" -> {
                ArrayNode original = a.deepCopy();
                for (JsonNode x : items) {
                    if (!contains(original, x)) {
                        a.add(x.deepCopy());
                    }
                }
            }
            case "insertArr" -> {
                int idx = o.path("index").asInt(a.size());
                if (idx < 0) {
                    idx = Math.max(0, a.size() + idx);
                }
                idx = Math.min(idx, a.size());
                for (JsonNode x : items) {
                    a.insert(idx++, x.deepCopy());
                }
            }
            case "replaceArr" -> {
                int i = find(a, o.path("replace"));
                if (i < 0) {
                    throw error(r, "_copy._mod." + p, "replaceArr target not found: " + o.path("replace"));
                }
                a.remove(i);
                for (int x = items.size() - 1; x >= 0; x--) {
                    a.insert(i, items.get(x).deepCopy());
                }
            }
            case "removeArr" -> {
                JsonNode names = o.path("names");
                if (names.isArray()) {
                    for (JsonNode nm : names) {
                        int i = findName(a, nm.asText());
                        if (i < 0 && !o.path("force").asBoolean(false)) {
                            throw error(r, "_copy._mod." + p, "removeArr name not found: " + nm);
                        }
                        if (i >= 0) {
                            a.remove(i);
                        }
                    }
                }
                JsonNode vals = o.path("items");
                if (vals.isArray()) {
                    for (JsonNode v : vals) {
                        int i = indexOf(a, v);
                        if (i < 0 && !o.path("force").asBoolean(false)) {
                            throw error(r, "_copy._mod." + p, "removeArr item not found: " + v);
                        }
                        if (i >= 0) {
                            a.remove(i);
                        }
                    }
                }
            }
            case "renameArr" -> {
                JsonNode ren = o.path("renames");
                List<JsonNode> changes = ren.isArray() ? toList(ren) : ren.isObject() ? List.of(ren) : List.of();
                for (JsonNode x : changes) {
                    int i = findName(a, x.path("rename").asText());
                    if (i < 0) {
                        throw error(r, "_copy._mod." + p, "renameArr name not found");
                    }
                    ((ObjectNode) a.get(i)).put("name", x.path("with").asText());
                }
            }
        }
    }

    private ArrayNode payloadItems(JsonNode o) {
        JsonNode v = o.get("items");
        if (v == null) {
            v = o.get("item");
        }
        ArrayNode a = mapper.createArrayNode();
        if (v == null) {
            return a;
        }
        if (v.isArray()) {
            v.forEach(x -> a.add(x.deepCopy()));
        } else {
            a.add(v.deepCopy());
        }
        return a;
    }

    private int find(ArrayNode a, JsonNode selector) {
        if (selector.isObject() && selector.has("index")) {
            return selector.path("index").asInt(-1);
        }
        if (selector.isObject() && selector.has("regex")) {
            Pattern p = Pattern.compile(selector.path("regex").asText(), selector.path("flags").asText().contains("i") ? Pattern.CASE_INSENSITIVE : 0);
            for (int i = 0; i < a.size(); i++) {
                if (p.matcher(a.get(i).path("name").asText()).find()) {
                    return i;
                }
            }
            return -1;
        }
        return findName(a, selector.asText());
    }

    private int findName(ArrayNode a, String s) {
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).path("name").asText().equals(s)) {
                return i;
            }
        }
        return -1;
    }

    private int indexOf(ArrayNode a, JsonNode n) {
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).equals(n)) {
                return i;
            }
        }
        return -1;
    }

    private boolean contains(ArrayNode a, JsonNode n) {
        return indexOf(a, n) >= 0;
    }

    private void textMode(JsonNode target, JsonNode op, MonsterSourceRecord r, String path) {
        String replace = reqText(op, "replace", r, path, "replaceTxt"), with = op.path("with").asText("");
        String flags = op.path("flags").asText("");
        int f = 0;
        if (flags.contains("i")) {
            f |= Pattern.CASE_INSENSITIVE;
        }
        if (flags.contains("s")) {
            f |= Pattern.DOTALL;
        }
        if (flags.contains("m")) {
            f |= Pattern.MULTILINE;
        }
        Pattern p = Pattern.compile(replace, f);
        Set<String> props = new HashSet<>();
        JsonNode ps = op.path("props");
        if (ps.isArray()) {
            ps.forEach(x -> props.add(x.asText()));
        }
        transformText(target, p, with, props, op.path("tagInsensitive").asBoolean(false));
    }

    private void transformText(JsonNode n, Pattern p, String with, Set<String> props, boolean insensitive) {
        if (n instanceof ObjectNode o) {
            List<String> keys = new ArrayList<>();
            o.fieldNames().forEachRemaining(keys::add);
            for (String k : keys) {
                JsonNode v = o.get(k);
                if (v.isTextual() && (props.isEmpty() || props.contains(k))) {
                    o.put(k, insensitive ? replaceJs(v.asText(), p, with) : replaceOutsideTags(v.asText(), p, with));
                } else if (v.isArray() && (props.isEmpty() || props.contains(k))) {
                    transformText(v, p, with, Set.of(), insensitive);
                } else if (v.isObject()) {
                    transformText(v, p, with, props, insensitive);
                }
            }
        } else if (n instanceof ArrayNode a) {
            for (int i = 0; i < a.size(); i++) {
                JsonNode v = a.get(i);
                if (v.isTextual()) {
                    a.set(i, TextNode.valueOf(insensitive ? replaceJs(v.asText(), p, with) : replaceOutsideTags(v.asText(), p, with)));
                } else {
                    transformText(v, p, with, props, insensitive);
                }
            }
        }
    }

    private String replaceOutsideTags(String s, Pattern p, String with) {
        StringBuilder out = new StringBuilder();
        int from = 0;
        while (from < s.length()) {
            int st = s.indexOf("{@", from);
            if (st < 0) {
                out.append(replaceJs(s.substring(from), p, with));
                break;
            }
            out.append(replaceJs(s.substring(from, st), p, with));
            int end = findTagEnd(s, st);
            if (end < 0) {
                out.append(s.substring(st));
                break;
            }
            out.append(s, st, end + 1);
            from = end + 1;
        }
        return out.toString();
    }

    private int findTagEnd(String s, int from) {
        int depth = 0;
        for (int i = from; i < s.length(); i++) {
            if (s.charAt(i) == '{') {
                depth++;
            } else if (s.charAt(i) == '}' && --depth == 0) {
                return i;
            }
        }
        return -1;
    }

    private String replaceJs(String s, Pattern p, String replacement) {
        Matcher m = p.matcher(s);
        StringBuilder out = new StringBuilder();
        int cursor = 0;
        while (m.find()) {
            out.append(s, cursor, m.start());
            out.append(expandJsReplacement(s, m, replacement));
            cursor = m.end();
        }
        out.append(s, cursor, s.length());
        return out.toString();
    }

    private String expandJsReplacement(String source, Matcher m, String r) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < r.length(); i++) {
            char c = r.charAt(i);
            if (c != '$' || i + 1 >= r.length()) {
                b.append(c);
                continue;
            }
            char n = r.charAt(i + 1);
            if (n == '$') {
                b.append('$');
                i++;
            } else if (n == '&') {
                b.append(m.group());
                i++;
            } else if (n == '`') {
                b.append(source, 0, m.start());
                i++;
            } else if (n == '\'') {
                b.append(source.substring(m.end()));
                i++;
            } else if (Character.isDigit(n)) {
                int g = n - '0';
                if (g > 0 && g <= m.groupCount()) {
                    b.append(Objects.toString(m.group(g), ""));
                    i++;
                    if (i + 1 < r.length() && Character.isDigit(r.charAt(i + 1)) && g * 10 + (r.charAt(i + 1) - '0') <= m.groupCount()) {
                        b.append(m.group(++i));
                    }
                } else {
                    b.append('$');
                }
            } else {
                b.append('$');
            }
        }
        return b.toString();
    }

    private void senses(ObjectNode root, JsonNode o) {
        ArrayNode a;
        if (root.path("senses").isArray()) {
            a = (ArrayNode) root.get("senses");
        } else {
            a = root.putArray("senses");
        }
        JsonNode ss = o.path("senses");
        List<JsonNode> vals = ss.isArray() ? toList(ss) : List.of(ss);
        for (JsonNode v : vals) {
            String type = v.path("type").asText();
            int range = v.path("range").asInt();
            Pattern p = Pattern.compile("(?i)^" + Pattern.quote(type) + "\\s+(\\d+)");
            boolean found = false;
            for (int i = 0; i < a.size(); i++) {
                Matcher m = p.matcher(a.get(i).asText());
                if (m.find()) {
                    found = true;
                    if (Integer.parseInt(m.group(1)) < range) {
                        a.set(i, TextNode.valueOf(type + " " + range + " ft."));
                    }
                    break;
                }
            }
            if (!found) {
                a.add(type + " " + range + " ft.");
            }
        }
    }

    private List<JsonNode> toList(JsonNode n) {
        List<JsonNode> a = new ArrayList<>();
        n.forEach(a::add);
        return a;
    }

    private void maxSize(ObjectNode root, JsonNode o) {
        String max = o.path("max").asText();
        Map<String, Integer> rank = Map.of("T", 0, "S", 1, "M", 2, "L", 3, "H", 4, "G", 5);
        JsonNode size = root.path("size");
        if (!size.isArray()) {
            return;
        }
        ArrayNode keep = mapper.createArrayNode();
        for (JsonNode x : size) {
            if (!rank.containsKey(x.asText()) || !rank.containsKey(max)) {
                throw new MonsterResolutionException("Unknown size code");
            }
            if (rank.get(x.asText()) <= rank.get(max)) {
                keep.add(x);
            }
        }
        if (keep.isEmpty()) {
            keep.add(max);
        }
        root.set("size", keep);
    }

    private void stringWrap(ObjectNode root, String base, JsonNode o) {
        String prop = o.path("prop").asText();
        String full = prop.isEmpty() ? base : (base.equals("_") ? prop : base + "." + prop);
        JsonNode n = atPath(root, full);
        if (n != null && n.isTextual()) {
            String pre = o.path("prefix").asText(""), suf = o.path("suffix").asText("");
            setExistingPath(root, full, TextNode.valueOf(pre + n.asText() + suf));
        }
    }

    private void scalarProps(ObjectNode root, String target, JsonNode o, String mode) {
        String prop = o.path("prop").asText("*");
        JsonNode n = atPath(root, target);
        if (!(n instanceof ObjectNode ob)) {
            return;
        }
        double scalar = o.path("scalar").asDouble();
        List<String> keys = new ArrayList<>();
        if (prop.equals("*")) {
            ob.fieldNames().forEachRemaining(keys::add);
        } else {
            keys.add(prop);
        }
        for (String k : keys) {
            JsonNode old = ob.get(k);
            if (old == null || !old.isNumber() && !old.isTextual()) {
                continue;
            }
            double v = old.isNumber() ? old.asDouble() : parseNum(old.asText());
            v = mode.equals("scalarAddProp") ? v + scalar : v * scalar;
            if (mode.equals("scalarMultProp") && o.path("floor").asBoolean(false)) {
                v = Math.floor(v);
            }
            ob.set(k, formatScalar(v, old));
        }
    }

    private void scalarXp(ObjectNode root, JsonNode o) {
        JsonNode cr = root.get("cr");
        if (cr == null) {
            return;
        }
        double scalar = o.path("scalar").asDouble();
        JsonNode xp = cr.isObject() ? cr.get("xp") : null;
        String crValue = cr.isObject() ? cr.path("cr").asText() : cr.asText();
        int base = xp != null && xp.isNumber() ? xp.asInt() : xpForCr(crValue);
        if (base < 0) {
            return;
        }
        double v = base * scalar;
        if (o.path("floor").asBoolean(false)) {
            v = Math.floor(v);
        }
        if (cr.isObject()) {
            ((ObjectNode) cr).put("xp", (int) v);
        } else {
            ObjectNode replacement = mapper.createObjectNode().put("cr", crValue).put("xp", (int) v);
            root.set("cr", replacement);
        }
    }

    private int xpForCr(String cr) {
        return switch (cr) {
            case "0" -> 10;
            case "1/8" -> 25;
            case "1/4" -> 50;
            case "1/2" -> 100;
            case "1" -> 200;
            case "2" -> 450;
            case "3" -> 700;
            case "4" -> 1100;
            case "5" -> 1800;
            case "6" -> 2300;
            case "7" -> 2900;
            case "8" -> 3900;
            case "9" -> 5000;
            case "10" -> 5900;
            case "11" -> 7200;
            case "12" -> 8400;
            case "13" -> 10000;
            case "14" -> 11500;
            case "15" -> 13000;
            case "16" -> 15000;
            case "17" -> 18000;
            case "18" -> 20000;
            case "19" -> 22000;
            case "20" -> 25000;
            case "21" -> 33000;
            case "22" -> 41000;
            case "23" -> 50000;
            case "24" -> 62000;
            case "25" -> 75000;
            case "26" -> 90000;
            case "27" -> 105000;
            case "28" -> 120000;
            case "29" -> 135000;
            case "30" -> 155000;
            default -> -1;
        };
    }

    private double parseNum(String s) {
        try {
            String v = s.replace("+", "");
            if (v.contains("/")) {
                String[] p = v.split("/");
                return Double.parseDouble(p[0]) / Double.parseDouble(p[1]);
            }
            return Double.parseDouble(v);
        } catch (Exception e) {
            return 0;
        }
    }

    private JsonNode formatScalar(double v, JsonNode old) {
        if (old.isNumber()) {
            return DecimalNode.valueOf(java.math.BigDecimal.valueOf(v).stripTrailingZeros());
        }
        String s = (v >= 0 ? "+" : "") + (v == (long) v ? Long.toString((long) v) : Double.toString(v));
        return TextNode.valueOf(s);
    }

    private void scalarTags(JsonNode node, JsonNode o, String mode) {
        if (node instanceof ObjectNode ob) {
            List<String> ks = new ArrayList<>();
            ob.fieldNames().forEachRemaining(ks::add);
            for (String k : ks) {
                JsonNode v = ob.get(k);
                if (v.isTextual()) {
                    ob.put(k, modifyTag(v.asText(), o, mode));
                } else {
                    scalarTags(v, o, mode);
                }
            }
        } else if (node instanceof ArrayNode a) {
            for (int i = 0; i < a.size(); i++) {
                JsonNode v = a.get(i);
                if (v.isTextual()) {
                    a.set(i, TextNode.valueOf(modifyTag(v.asText(), o, mode)));
                } else {
                    scalarTags(v, o, mode);
                }
            }
        }
    }

    private String modifyTag(String s, JsonNode o, String mode) {
        String tag = mode.equals("scalarAddHit") ? "hit" : "dc";
        Pattern p = Pattern.compile("\\{@" + tag + " ([+-]?\\d+)(?:\\|[^}]*)?}");
        Matcher m = p.matcher(s);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            int v = Integer.parseInt(m.group(1)) + (int) o.path("scalar").asInt();
            m.appendReplacement(b, Matcher.quoteReplacement("{@" + tag + " " + v + "}"));
        }
        m.appendTail(b);
        return b.toString();
    }

    private void addSkills(ObjectNode root, JsonNode op) {
        JsonNode skills = op.path("skills");
        if (!skills.isObject()) {
            return;
        }
        JsonNode cr = root.path("cr");
        double challenge = parseNum(cr.isObject() ? cr.path("cr").asText() : cr.asText());
        int pb = challenge < 5 ? 2 : challenge < 9 ? 3 : challenge < 13 ? 4 : challenge < 17 ? 5 : challenge < 21 ? 6 : challenge < 25 ? 7 : challenge < 29 ? 8 : 9;
        ObjectNode dst = root.with("skill");
        skills.fields().forEachRemaining(e -> {
            int mode = e.getValue().isNumber() ? e.getValue().asInt() : e.getValue().path("value").asInt();
            String ability = abilityForSkill(e.getKey());
            int score = root.path(ability).asInt(10);
            int bonus = (int) Math.floor((score - 10) / 2.0) + pb * mode;
            JsonNode old = dst.get(e.getKey());
            int oldVal = old == null ? Integer.MIN_VALUE : (int) parseNum(old.asText());
            if (bonus > oldVal) {
                dst.put(e.getKey(), (bonus >= 0 ? "+" : "") + bonus);
            }
        });
    }

    private String abilityForSkill(String s) {
        return switch (s.toLowerCase(Locale.ROOT)) {
            case "athletics" -> "str";
            case "acrobatics", "sleight of hand", "stealth" -> "dex";
            case "arcana", "history", "investigation", "nature", "religion" -> "int";
            case "animal handling", "insight", "medicine", "perception", "survival" -> "wis";
            default -> "cha";
        };
    }

    private void spellMode(ObjectNode root, JsonNode op, String mode) {
        JsonNode cast = root.path("spellcasting");
        if (!cast.isArray() || cast.isEmpty()) {
            return;
        }
        String wanted = op.path("name").asText();
        ObjectNode sc = null;
        for (JsonNode x : cast) {
            if (x instanceof ObjectNode o && (wanted.isEmpty() || wanted.equals(o.path("name").asText()))) {
                sc = o;
                break;
            }
        }
        if (sc == null) {
            return;
        }
        if (mode.equals("addSpells")) {
            JsonNode incoming = op.path("spells");
            if (incoming.isObject()) {
                ObjectNode levels = sc.with("spells");
                incoming.fields().forEachRemaining(le -> {
                    ObjectNode dst = levels.with(le.getKey());
                    JsonNode val = le.getValue();
                    val.fields().forEachRemaining(cat -> {
                        JsonNode old = dst.get(cat.getKey());
                        if (old != null && old.isArray() && cat.getValue().isArray()) {
                            ArrayNode merged = (ArrayNode) old;
                            cat.getValue().forEach(v -> merged.add(v.deepCopy()));
                            sortTextArray(merged);
                        } else {
                            dst.set(cat.getKey(), cat.getValue().deepCopy());
                        }
                    });
                });
            }
            for (String bucket : List.of("will", "constant", "ritual", "daily", "yearly", "rest", "restLong", "recharge", "legendary", "charges", "weekly", "monthly")) {
                JsonNode add = op.get(bucket);
                if (add == null) {
                    continue;
                }
                if (add.isArray()) {
                    ArrayNode dst = sc.withArray(bucket);
                    add.forEach(v -> dst.add(v.deepCopy()));
                    sortTextArray(dst);
                } else if (add.isObject()) {
                    ObjectNode dst = sc.with(bucket);
                    add.fields().forEachRemaining(e -> {
                        ArrayNode a = dst.withArray(e.getKey());
                        e.getValue().forEach(v -> a.add(v.deepCopy()));
                        sortTextArray(a);
                    });
                } else {
                    sc.set(bucket, add.deepCopy());
                }
            }
        } else if (mode.equals("replaceSpells")) {
            replaceSpellBuckets(sc, op, "spells", false);
            replaceSpellBuckets(sc, op, "daily", false);
        } else {
            for (String bucket : List.of("spells", "daily", "yearly", "rest", "restLong", "recharge", "legendary", "charges", "weekly", "monthly")) {
                removeSpellBuckets(sc, op.path(bucket), bucket);
            }
            // Pinned v2.37.0 calls filter without assigning for these three buckets; retain that behavior.
        }
    }

    private void replaceSpellBuckets(ObjectNode sc, JsonNode op, String bucket, boolean ignored) {
        JsonNode groups = op.path(bucket);
        if (!groups.isObject()) {
            return;
        }
        groups.fields().forEachRemaining(group -> {
            JsonNode a = bucket.equals("spells") ? sc.path("spells").path(group.getKey()).path("spells") : sc.path(bucket).path(group.getKey());
            if (!(a instanceof ArrayNode arr)) {
                return;
            }
            for (JsonNode change : group.getValue()) {
                String from = change.path("replace").asText(), to = change.path("with").asText();
                int i = indexOfText(arr, from);
                if (i < 0) {
                    throw new MonsterResolutionException("replaceSpells could not find " + from + " in " + bucket + "[" + group.getKey() + "]");
                }
                arr.set(i, TextNode.valueOf(to));
            }
            sortTextArray(arr);
        });
    }

    private void removeSpellBuckets(ObjectNode sc, JsonNode groups, String bucket) {
        if (!groups.isObject()) {
            return;
        }
        groups.fields().forEachRemaining(group -> {
            JsonNode a = bucket.equals("spells") ? sc.path("spells").path(group.getKey()).path("spells") : sc.path(bucket).path(group.getKey());
            if (a instanceof ArrayNode arr && !(bucket.equals("will") || bucket.equals("constant") || bucket.equals("ritual"))) {
                group.getValue().forEach(v -> {
                    int i = indexOfText(arr, v.asText());
                    if (i >= 0) {
                        arr.remove(i);
                    }
                });
            }
        });
    }

    private int indexOfText(ArrayNode a, String text) {
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).isTextual() && a.get(i).asText().equals(text)) {
                return i;
            }
        }
        return -1;
    }

    private void sortTextArray(ArrayNode a) {
        List<JsonNode> vals = new ArrayList<>();
        a.forEach(x -> vals.add(x.deepCopy()));
        vals.sort(Comparator.comparing(x -> x.asText().toLowerCase(Locale.ROOT)));
        a.removeAll();
        vals.forEach(a::add);
    }

    private JsonNode atPath(JsonNode root, String path) {
        JsonNode n = root;
        for (String p : path.split("\\.")) {
            if (n == null) {
                return null;
            }
            n = n.get(p);
        }
        return n;
    }

    private void setExistingPath(ObjectNode root, String p, JsonNode v) {
        String[] a = p.split("\\.");
        JsonNode n = root;
        for (int i = 0; i < a.length - 1; i++) {
            n = n.path(a[i]);
        }
        if (n instanceof ObjectNode o) {
            o.set(a[a.length - 1], v);
        }
    }

    private void removePath(ObjectNode root, String p) {
        String[] a = p.split("\\.");
        JsonNode n = root;
        for (int i = 0; i < a.length - 1; i++) {
            n = n.path(a[i]);
        }
        if (n instanceof ObjectNode o) {
            o.remove(a[a.length - 1]);
        }
    }

    private String reqText(JsonNode n, String k, MonsterSourceRecord r, String p, String m) {
        if (!n.path(k).isTextual()) {
            throw error(r, "_copy._mod." + p + "/" + m, "requires string " + k);
        }
        return n.path(k).asText();
    }

    private String sourceLabel(MonsterSourceRecord r) {
        ObjectNode source = r.original();
        return source.path("name").asText(r.identity().name()) + "|" + source.path("source").asText(r.identity().source()) + " at " + r.location();
    }

    private MonsterResolutionException error(MonsterSourceRecord r, String p, String m) {
        return new MonsterResolutionException(sourceLabel(r) + " " + p + ": " + m);
    }

    private MonsterResolutionException error(MonsterSourceRecord r, String p, String m, Throwable t) {
        return new MonsterResolutionException(sourceLabel(r) + " " + p + ": " + m, t);
    }
}
