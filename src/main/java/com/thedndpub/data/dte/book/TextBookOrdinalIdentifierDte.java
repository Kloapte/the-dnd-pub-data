package com.thedndpub.data.dte.book;

public record TextBookOrdinalIdentifierDte(String text) implements BookOrdinalIdentifierDte {
    @Override
    public String value() {
        return text;
    }
}
