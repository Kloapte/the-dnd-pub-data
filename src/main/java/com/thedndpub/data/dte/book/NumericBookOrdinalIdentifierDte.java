package com.thedndpub.data.dte.book;

public record NumericBookOrdinalIdentifierDte(Integer number) implements BookOrdinalIdentifierDte {
    @Override
    public String value() {
        return number.toString();
    }
}
