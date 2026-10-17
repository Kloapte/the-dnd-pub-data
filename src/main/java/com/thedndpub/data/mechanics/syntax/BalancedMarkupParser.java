package com.thedndpub.data.mechanics.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Stateless syntactic parser; does not interpret renderer defaults or game rules. */
public final class BalancedMarkupParser {
    public static final int MAX_TAG_DEPTH = 64;

    public MarkupParseResult parse(SyntaxText input) {
        Objects.requireNonNull(input, "input");
        return new MarkupParseResult(tags(input, 0, input.text().length(), 1));
    }

    private List<MarkupTag> tags(SyntaxText input, int start, int limit, int depth) {
        List<MarkupTag> result = new ArrayList<>();
        int cursor = start;
        while (cursor < limit) {
            int opening = input.text().indexOf("{@", cursor);
            if (opening < 0 || opening >= limit) {
                break;
            }
            if (depth > MAX_TAG_DEPTH) {
                throw error(input, opening, limit, "Tag nesting exceeds " + MAX_TAG_DEPTH);
            }
            MarkupTag tag = tag(input, opening, limit, depth);
            result.add(tag);
            cursor = tag.evidence().span().endExclusive();
        }
        return List.copyOf(result);
    }

    private MarkupTag tag(SyntaxText input, int opening, int limit, int depth) {
        String text = input.text();
        int braces = 1;
        int closing = opening + 2;
        for (; closing < limit; closing++) {
            char character = text.charAt(closing);
            if (character == '{') {
                braces++;
            } else if (character == '}' && --braces == 0) {
                break;
            }
        }
        if (braces != 0) {
            throw error(input, opening, limit, "Unclosed markup tag");
        }
        int nameEnd = opening + 2;
        while (nameEnd < closing && nameCharacter(text.charAt(nameEnd))) {
            nameEnd++;
        }
        if (nameEnd == opening + 2 || (nameEnd < closing
                && !separator(text.charAt(nameEnd)) && text.charAt(nameEnd) != '|')) {
            throw error(input, opening, closing + 1, "Invalid markup tag name or separator");
        }
        String name = text.substring(opening + 2, nameEnd);
        String rawArguments = text.substring(nameEnd, closing);
        SyntaxEvidence evidence = new SyntaxEvidence(input.location(), new SyntaxSpan(opening, closing + 1));
        return new MarkupTag(evidence, text.substring(opening, closing + 1), name, rawArguments,
                arguments(rawArguments), tags(input, nameEnd, closing, depth + 1));
    }

    private List<String> arguments(String rawArguments) {
        int start = 0;
        while (start < rawArguments.length() && separator(rawArguments.charAt(start))) {
            start++;
        }
        if (start == rawArguments.length()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        int braces = 0;
        int segmentStart = start;
        for (int index = start; index < rawArguments.length(); index++) {
            char character = rawArguments.charAt(index);
            if (character == '{') {
                braces++;
            } else if (character == '}') {
                braces--;
            } else if (character == '|' && braces == 0) {
                result.add(rawArguments.substring(segmentStart, index));
                segmentStart = index + 1;
            }
        }
        result.add(rawArguments.substring(segmentStart));
        return List.copyOf(result);
    }

    private static boolean nameCharacter(char value) {
        return value >= 'a' && value <= 'z' || value >= 'A' && value <= 'Z'
                || value >= '0' && value <= '9' || value == '_' || value == '-';
    }

    private static boolean separator(char value) {
        return Character.isWhitespace(value) || Character.isSpaceChar(value) || value == '\uFEFF';
    }

    private static MarkupSyntaxException error(SyntaxText input, int start, int end, String reason) {
        return new MarkupSyntaxException(reason,
                new SyntaxEvidence(input.location(), new SyntaxSpan(start, end)));
    }
}
