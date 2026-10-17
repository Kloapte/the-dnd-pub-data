package com.thedndpub.data.mechanics.syntax;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BalancedMarkupParserTests {
    private final BalancedMarkupParser parser = new BalancedMarkupParser();
    private final SyntaxObservationClassifier classifier = new SyntaxObservationClassifier();

    @Test
    void preservesTheExactTagNameArgumentSuffixAndOriginalSpan() {
        SyntaxText text = input("before {@DaMage  8d6 |display|} after");
        MarkupTag tag = parser.parse(text).tags().getFirst();
        assertEquals("DaMage", tag.name());
        assertEquals("  8d6 |display|", tag.rawArgumentText());
        assertEquals(List.of("8d6 ", "display", ""), tag.arguments());
        assertEquals("{@DaMage  8d6 |display|}", tag.evidence().span().extract(text.text()));
        assertEquals(tag.originalText(), tag.evidence().span().extract(text.text()));
        assertEquals(text.location(), tag.evidence().location());
    }

    @Test
    void nestedTagsDoNotSplitTheirParentsArguments() {
        MarkupParseResult parsed = parser.parse(input("{@note use {@dice 1d6|display} or {@spell Shield|PHB}|extra}"));
        MarkupTag parent = parsed.tags().getFirst();
        assertEquals(List.of("use {@dice 1d6|display} or {@spell Shield|PHB}", "extra"), parent.arguments());
        assertEquals(List.of("note", "dice", "spell"), parsed.preorder().stream().map(MarkupTag::name).toList());
        assertEquals(2, parent.children().size());
        assertEquals(List.of("1d6", "display"), parent.children().getFirst().arguments());
    }

    @Test
    void adjacentAndSeparatedTagsKeepOriginalOrdering() {
        MarkupParseResult parsed = parser.parse(input("x{@atk mw}{@hit 7} then {@h} {@damage 2d6 + 4}y"));
        assertEquals(List.of("atk", "hit", "h", "damage"), parsed.tags().stream().map(MarkupTag::name).toList());
        assertEquals(parsed, parser.parse(input("x{@atk mw}{@hit 7} then {@h} {@damage 2d6 + 4}y")));
    }

    @Test
    void emptyTrailingAndExtraArgumentsRemainDistinct() {
        assertEquals(List.of("Shield", "", "label", ""),
                parser.parse(input("{@spell Shield||label|}")).tags().getFirst().arguments());
        assertEquals(List.of("", "", ""), parser.parse(input("{@future ||}")).tags().getFirst().arguments());
        assertEquals(List.of(), parser.parse(input("{@h}")).tags().getFirst().arguments());
        MarkupTag whitespace = parser.parse(input("{@future  }")).tags().getFirst();
        assertEquals("  ", whitespace.rawArgumentText());
        assertEquals(List.of(), whitespace.arguments());
        assertEquals(List.of("", "x"), parser.parse(input("{@future|x}")).tags().getFirst().arguments());
    }

    @Test
    void offsetsUseUtf16RatherThanUnicodeCodePoints() {
        SyntaxText text = input("😀é {@damage 1d6|🔥} adjacent {@h}");
        List<MarkupTag> tags = parser.parse(text).tags();
        assertEquals(4, tags.getFirst().evidence().span().startInclusive());
        assertEquals("{@damage 1d6|🔥}", tags.getFirst().evidence().span().extract(text.text()));
        assertEquals(text.text().indexOf("{@h}"), tags.getLast().evidence().span().startInclusive());
    }

    @Test
    void malformedTagsFailWithEvidenceInsteadOfReturningPartialResults() {
        for (String value : List.of("{@dice 1d6", "valid {@h} then {@dice", "{@}", "{@ dice 1d6}",
                "{@name! value}", "{@note {@dice 1d6}")) {
            SyntaxText text = input(value);
            MarkupSyntaxException error = assertThrows(MarkupSyntaxException.class, () -> parser.parse(text));
            assertEquals(text.location(), error.evidence().location());
            assertTrue(error.evidence().span().startInclusive() >= 0);
            assertTrue(error.evidence().span().endExclusive() <= value.length());
        }
    }

    @Test
    void excessiveTagNestingFailsAtTheDeclaredLimit() {
        String text = "{@note ".repeat(BalancedMarkupParser.MAX_TAG_DEPTH + 1)
                + "value" + "}".repeat(BalancedMarkupParser.MAX_TAG_DEPTH + 1);
        assertThrows(MarkupSyntaxException.class, () -> parser.parse(input(text)));
    }

    @Test
    void ordinaryTextAndBracesOutsideTagsAreNotRepairedOrInterpreted() {
        assertEquals(List.of(), parser.parse(input("plain } { text and 2d6 + 3")).tags());
        MarkupTag tag = parser.parse(input("{@future {value|other}|last}")).tags().getFirst();
        assertEquals(List.of("{value|other}", "last"), tag.arguments());
    }

    @Test
    void backslashesArePreservedWithoutInventingRendererEscapes() {
        MarkupTag tag = parser.parse(input("{@future a\\|b}")).tags().getFirst();
        assertEquals(List.of("a\\", "b"), tag.arguments());
        assertEquals("{@future a\\|b}", tag.originalText());
    }

    @Test
    void unsupportedReferenceAndFormattingTagsRemainOpaque() {
        List<SyntaxObservation> observations = classifier.classify(parser.parse(
                input("{@spell Shield|PHB}{@b example}{@future value}")));
        assertEquals(List.of(OpaqueTagCategory.REFERENCE, OpaqueTagCategory.FORMATTING, OpaqueTagCategory.UNSUPPORTED),
                observations.stream().map(value -> assertInstanceOf(OpaqueSyntaxObservation.class, value).category())
                        .toList());
        assertEquals("Shield", observations.getFirst().tag().arguments().getFirst());
    }

    @Test
    void formulaShapesRemainDifferentFromGameplayMeaning() {
        List<String> tokens = List.of("+7", "-2", "0", "8d6", "2d6 + 4", "1d4 + PB", "<$to_hit__str$>", "");
        assertEquals(List.of(SyntaxExpressionKind.SIGNED_INTEGER, SyntaxExpressionKind.SIGNED_INTEGER,
                        SyntaxExpressionKind.UNSIGNED_INTEGER, SyntaxExpressionKind.DICE_NOTATION,
                        SyntaxExpressionKind.DICE_NOTATION, SyntaxExpressionKind.OPAQUE_OR_SYMBOLIC,
                        SyntaxExpressionKind.OPAQUE_OR_SYMBOLIC, SyntaxExpressionKind.OPAQUE_OR_SYMBOLIC),
                tokens.stream().map(SyntaxExpression::recognize).map(SyntaxExpression::kind).toList());
        for (String token : tokens) {
            assertEquals(token, SyntaxExpression.recognize(token).authoredToken());
        }
    }

    @Test
    void scaledFormulaArgumentsStayAuthoredRatherThanCalculated() {
        FormulaSyntaxObservation value = assertInstanceOf(FormulaSyntaxObservation.class,
                classifier.classify(parser.parse(input("{@scaledamage 8d6|3-9|1d6}"))).getFirst());
        assertEquals(List.of("8d6", "3-9", "1d6"), value.tag().arguments());
        assertEquals("8d6", value.expression().authoredToken());
    }

    @Test
    void modifierTagsPreserveSignedZeroAndSymbolicVariants() {
        for (String token : List.of("+7", "-1", "0", "<$to_hit__str$>")) {
            ModifierSyntaxObservation value = assertInstanceOf(ModifierSyntaxObservation.class,
                    classifier.classify(parser.parse(input("{@hit " + token + "}"))).getFirst());
            assertEquals(token, value.expression().authoredToken());
        }
        assertInstanceOf(OpaqueSyntaxObservation.class,
                classifier.classify(parser.parse(input("{@damage}"))).getFirst());
    }

    @Test
    void labelArgumentsAreNotInterpretedAsOutcomesOrRechargeDefaults() {
        for (String value : List.of("{@recharge}", "{@recharge 5|m}", "{@actSave dex}", "{@actSaveFailBy 5}",
                "{@actResponse qualifier}", "{@hitYourSpellAttack}")) {
            LabelSyntaxObservation observation = assertInstanceOf(LabelSyntaxObservation.class,
                    classifier.classify(parser.parse(input(value))).getFirst());
            assertEquals(value, observation.tag().originalText());
        }
    }

    @Test
    void observationAndTagCollectionsAreImmutable() {
        MarkupParseResult parsed = parser.parse(input("{@note {@dice 1d6}}"));
        assertThrows(UnsupportedOperationException.class, () -> parsed.tags().clear());
        assertThrows(UnsupportedOperationException.class, () -> parsed.preorder().clear());
        assertThrows(UnsupportedOperationException.class, () -> parsed.tags().getFirst().arguments().clear());
        assertThrows(UnsupportedOperationException.class, () -> parsed.tags().getFirst().children().clear());
        assertThrows(UnsupportedOperationException.class, () -> classifier.classify(parsed).clear());
        List<MarkupTag> original = new ArrayList<>(parsed.tags());
        MarkupParseResult copied = new MarkupParseResult(original);
        original.clear();
        assertEquals(1, copied.tags().size());
    }

    static SyntaxText input(String value) {
        SyntaxSourceOwner owner = new SyntaxSourceOwner("data/example.json", null, "example", "/example/0");
        return new SyntaxText(new SyntaxTextLocation(owner, "/example/0/entries/0", List.of(), SyntaxOrigin.AUTHORED), value);
    }
}
