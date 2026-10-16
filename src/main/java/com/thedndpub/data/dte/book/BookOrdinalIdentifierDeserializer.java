package com.thedndpub.data.dte.book;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public final class BookOrdinalIdentifierDeserializer extends JsonDeserializer<BookOrdinalIdentifierDte> {
    @Override
    public BookOrdinalIdentifierDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            return new NumericBookOrdinalIdentifierDte(parser.getIntValue());
        }
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new TextBookOrdinalIdentifierDte(parser.getValueAsString());
        }
        throw JsonMappingException.from(
                parser,
                "Book ordinal identifier must be a string or integer; found " + parser.currentToken());
    }
}
