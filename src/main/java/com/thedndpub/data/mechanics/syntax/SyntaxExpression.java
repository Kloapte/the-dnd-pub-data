package com.thedndpub.data.mechanics.syntax;

import java.util.Objects;
import java.util.regex.Pattern;

/** A lexical shape only; no evaluation or complete formula grammar is implied. */
public record SyntaxExpression(String authoredToken, SyntaxExpressionKind kind) {
    private static final Pattern DICE = Pattern.compile(
            "[0-9]+[dD][0-9]+(?:[ \\t]*[+*-][ \\t]*(?:[0-9]+[dD][0-9]+|[0-9]+))*");

    public SyntaxExpression {
        Objects.requireNonNull(authoredToken, "authoredToken");
        Objects.requireNonNull(kind, "kind");
    }

    public static SyntaxExpression recognize(String token) {
        Objects.requireNonNull(token, "token");
        SyntaxExpressionKind kind;
        if (token.matches("[+-][0-9]+")) {
            kind = SyntaxExpressionKind.SIGNED_INTEGER;
        } else if (token.matches("[0-9]+")) {
            kind = SyntaxExpressionKind.UNSIGNED_INTEGER;
        } else if (DICE.matcher(token).matches()) {
            kind = SyntaxExpressionKind.DICE_NOTATION;
        } else {
            kind = SyntaxExpressionKind.OPAQUE_OR_SYMBOLIC;
        }
        return new SyntaxExpression(token, kind);
    }
}
