package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class MonsterJson {
    private MonsterJson() {
    }

    static JsonMappingException error(JsonParser p, String path, String message) {
        return JsonMappingException.from(p, path + " " + message);
    }

    static void fields(JsonNode n, Set<String> allowed, JsonParser p, String path) throws IOException {
        var it = n.fieldNames();
        while (it.hasNext()) {
            String f = it.next();
            if (!allowed.contains(f)) {
                throw error(p, path + "." + f, "unexpected property");
            }
        }
    }

    static String text(JsonNode n, String key, JsonParser p, String path) throws IOException {
        JsonNode v = n.get(key);
        if (v == null || v.isNull()) {
            return null;
        }
        if (!v.isTextual()) {
            throw error(p, path + "." + key, "must be a string");
        }
        return v.textValue();
    }

    static Integer integer(JsonNode n, String key, JsonParser p, String path) throws IOException {
        JsonNode v = n.get(key);
        if (v == null || v.isNull()) {
            return null;
        }
        if (!v.isIntegralNumber()) {
            throw error(p, path + "." + key, "must be an integer");
        }
        return v.intValue();
    }

    static Boolean bool(JsonNode n, String key, JsonParser p, String path) throws IOException {
        JsonNode v = n.get(key);
        if (v == null || v.isNull()) {
            return null;
        }
        if (!v.isBoolean()) {
            throw error(p, path + "." + key, "must be a boolean");
        }
        return v.booleanValue();
    }

    static List<String> strings(JsonNode n, String key, JsonParser p, String path) throws IOException {
        JsonNode v = n.get(key);
        if (v == null || v.isNull()) {
            return null;
        }
        if (!v.isArray()) {
            throw error(p, path + "." + key, "must be an array");
        }
        return stringsNode(v, p, path + "." + key);
    }

    static List<String> stringsNode(JsonNode v, JsonParser p, String path) throws IOException {
        if (!v.isArray()) {
            throw error(p, path, "must be an array");
        }
        List<String> out = new ArrayList<>();
        int i = 0;
        for (JsonNode x : v) {
            if (!x.isTextual()) {
                throw error(p, path + "[" + i + "]", "must be a string");
            }
            out.add(x.textValue());
            i++;
        }
        return List.copyOf(out);
    }
}
