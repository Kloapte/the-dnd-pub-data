package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.books.BookDte;
import com.thedndpub.data.dte.books.BooksWrapperDte;
import com.thedndpub.data.dte.items.ItemDte;
import com.thedndpub.data.dte.items.ItemsWrapperDte;
import com.thedndpub.data.util.records.ItemKey;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;

@Service
public class BooksService {
    @Getter
    private final HashMap<ItemKey, BookDte> books;

    public BooksService() {
        BooksWrapperDte wrapper = this.loadItemsFromJson();

        this.books = makeBooks(wrapper);
    }

    private HashMap<ItemKey, BookDte> makeBooks(BooksWrapperDte wrapper) {
        HashMap<ItemKey, BookDte> books = new HashMap<>();

        for(BookDte bookDte : wrapper.getBook()) {
            ItemKey key = new ItemKey(bookDte.getName(), bookDte.getSource());
            books.put(key, bookDte);
        }

        return books;
    }

    private BooksWrapperDte loadItemsFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data/books.json").getInputStream();
            return mapper.readValue(inputStream, BooksWrapperDte.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load books.json", e);
        }
    }
}
