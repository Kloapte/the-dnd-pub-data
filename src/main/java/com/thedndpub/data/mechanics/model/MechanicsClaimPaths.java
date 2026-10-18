package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/** Validates claim pointers against typed values using streaming JSON, without an untyped object model. */
final class MechanicsClaimPaths {
    private MechanicsClaimPaths() {
    }

    static Set<String> paths(MechanicRevisionContent content) {
        Set<String> paths = new HashSet<>();
        try (var parser = new JsonFactory().createParser(MechanicsHashes.claimBytes(content))) {
            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                if (token.isScalarValue() || token.isStructStart()) {
                    paths.add(parser.getParsingContext().pathAsPointer().toString());
                }
            }
            return paths;
        } catch (IOException exception) {
            throw new IllegalArgumentException("Cannot validate canonical claim paths", exception);
        }
    }
}
