package com.thedndpub.data.mechanics.syntax;

final class SyntaxPointers {
    private SyntaxPointers() {
    }

    static String requirePointer(String value) {
        if (value == null || (!value.isEmpty() && !value.startsWith("/"))) {
            throw new IllegalArgumentException("An RFC 6901 JSON Pointer is required");
        }
        for (int index = 0; index < value.length(); index++) {
            if (value.charAt(index) == '~') {
                index++;
                if (index == value.length() || (value.charAt(index) != '0' && value.charAt(index) != '1')) {
                    throw new IllegalArgumentException("Invalid JSON Pointer escape");
                }
            }
        }
        return value;
    }

    static String append(String pointer, String segment) {
        return pointer + "/" + segment.replace("~", "~0").replace("/", "~1");
    }

    static boolean contains(String parent, String child) {
        return child.equals(parent) || child.startsWith(parent + "/");
    }
}
