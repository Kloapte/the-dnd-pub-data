package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.items.ItemDte;
import com.thedndpub.data.dte.items.ItemsWrapperDte;
import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.util.records.ItemKey;
import com.thedndpub.data.util.records.RaceKey;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;

@Service
public class ItemsService {
    @Getter
    private final HashMap<ItemKey, ItemDte> items;

    public ItemsService() {
        ItemsWrapperDte wrapper = this.loadItemsFromJson();

        this.items = makeItems(wrapper);
    }

    private HashMap<ItemKey, ItemDte> makeItems(ItemsWrapperDte wrapper) {
        HashMap<ItemKey, ItemDte> items = new HashMap<>();

        for(ItemDte itemDte : wrapper.getItem()) {
            ItemKey key = new ItemKey(itemDte.getName(), itemDte.getSource());
            items.put(key, itemDte);
        }

        return items;
    }

    private ItemsWrapperDte loadItemsFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data/items.json").getInputStream();
            return mapper.readValue(inputStream, ItemsWrapperDte.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load items.json", e);
        }
    }
}
