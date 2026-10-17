package com.thedndpub.data.mechanics.syntax;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthoredSyntaxTextWalkerTests {
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthoredSyntaxTextWalker walker = new AuthoredSyntaxTextWalker();
    private final SyntaxSourceOwner owner = new SyntaxSourceOwner("data/example.json", null, "example", "/example/0");

    @Test
    void originalHierarchyAndEscapedPropertyNamesHaveReproduciblePointers() throws Exception {
        var tree = mapper.readTree("""
                {"type":"entries","a/b~c":[{"type":"list","items":["{@dice 1d6}"]}]}
                """);
        List<SyntaxText> texts = new ArrayList<>();
        walker.walk(tree, owner, texts::add);
        SyntaxText text = texts.getLast();
        assertEquals("/example/0/a~1b~0c/0/items/0", text.location().jsonPointer());
        assertEquals(List.of("entries", "list"), text.location().hierarchy().stream()
                .map(SyntaxContext::authoredType).filter(value -> value != null).toList());
        assertEquals(text.text(), tree.at(text.location().jsonPointer().substring(owner.jsonPointer().length())).textValue());
    }

    @Test
    void repeatedTextAtDifferentLocationsNeverCollapsesToOneOccurrence() throws Exception {
        List<SyntaxText> texts = new ArrayList<>();
        walker.walk(mapper.readTree("[\"{@h}\",\"{@h}\"]"), owner, texts::add);
        assertEquals(texts.getFirst().text(), texts.getLast().text());
        assertNotEquals(texts.getFirst().location(), texts.getLast().location());
        List<SyntaxText> repeated = new ArrayList<>();
        walker.walk(mapper.readTree("[\"{@h}\",\"{@h}\"]"), owner, repeated::add);
        assertEquals(texts, repeated);
    }

    @Test
    void copyPayloadsAreAuthoredInstructionsRatherThanEffectiveLineage() throws Exception {
        List<SyntaxText> texts = new ArrayList<>();
        walker.walk(mapper.readTree("""
                {"entries":["{@h}"],"_copy":{"_mod":{"entries":["{@damage 1d6}"]}},"value":0,"flag":false}
                """), owner, texts::add);
        assertEquals(2, texts.size());
        assertEquals(SyntaxOrigin.AUTHORED, texts.getFirst().location().origin());
        assertEquals(SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION, texts.getLast().location().origin());
        SyntaxTextLocation unknown = new SyntaxTextLocation(owner, "/example/0", List.of(),
                SyntaxOrigin.AUTHORED_ORIGIN_UNAVAILABLE);
        assertEquals(SyntaxOrigin.AUTHORED_ORIGIN_UNAVAILABLE, unknown.origin());
    }

    @Test
    void evidenceRejectsInvalidSpansPointersAndUnrelatedHierarchy() {
        assertThrows(IllegalArgumentException.class, () -> new SyntaxSpan(-1, 3));
        assertThrows(IllegalArgumentException.class, () -> new SyntaxSpan(3, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new SyntaxSourceOwner("data/example.json", null, "example", "$.example[0]"));
        assertThrows(IllegalArgumentException.class,
                () -> new SyntaxContext("/bad~escape", null));
        assertThrows(IllegalArgumentException.class,
                () -> new SyntaxTextLocation(owner, "/example/1", List.of(), SyntaxOrigin.AUTHORED));
        assertThrows(IllegalArgumentException.class,
                () -> new SyntaxTextLocation(owner, "/example/0/entries/0",
                        List.of(new SyntaxContext("/another", null)), SyntaxOrigin.AUTHORED));
    }

    @Test
    void contextCollectionsUseDefensiveCopies() {
        List<SyntaxContext> contexts = new ArrayList<>();
        contexts.add(new SyntaxContext("/example/0", "entries"));
        SyntaxTextLocation location = new SyntaxTextLocation(owner, "/example/0/entries/0", contexts, SyntaxOrigin.AUTHORED);
        contexts.clear();
        assertEquals(1, location.hierarchy().size());
        assertThrows(UnsupportedOperationException.class, () -> location.hierarchy().clear());
        assertTrue(location.owner().resourceSha256() == null);
    }
}
