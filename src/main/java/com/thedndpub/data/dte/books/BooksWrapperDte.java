package com.thedndpub.data.dte.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thedndpub.data.dte.items.ItemDte;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@JsonIgnoreProperties(ignoreUnknown = true)
public class BooksWrapperDte {
    private List<BookDte> book;
}
