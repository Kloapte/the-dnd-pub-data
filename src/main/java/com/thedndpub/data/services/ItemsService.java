package com.thedndpub.data.services;

import tools.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.items.ItemDte;
import com.thedndpub.data.dte.items.ItemsWrapperDte;
import com.thedndpub.data.util.records.ItemKey;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;

@Service
public class ItemsService {
    @Getter
    private final HashMap<ItemKey, ItemDte> items;
    @Getter
    private final HashMap<ItemKey, ItemDte> itemGroups;

    public ItemsService() {
        ItemsWrapperDte wrapper = this.loadItemsFromJson();

        this.items = makeItems(wrapper);
        this.itemGroups = makeItemGroups(wrapper);
    }

    private HashMap<ItemKey, ItemDte> makeItems(ItemsWrapperDte wrapper) {
        HashMap<ItemKey, ItemDte> items = new HashMap<>();

        for(ItemDte itemDte : wrapper.item()) {
            ItemKey key = new ItemKey(itemDte.name(), itemDte.source());
            items.put(key, itemDte);
        }

        return items;
    }

    private HashMap<ItemKey, ItemDte> makeItemGroups(ItemsWrapperDte wrapper) {
        HashMap<ItemKey, ItemDte> items = new HashMap<>();

        for(ItemDte itemDte : wrapper.itemGroup()) {
            ItemKey key = new ItemKey(itemDte.name(), itemDte.source());
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
