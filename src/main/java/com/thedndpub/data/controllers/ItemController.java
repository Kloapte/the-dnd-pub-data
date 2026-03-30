
package com.thedndpub.data.controllers;

import com.thedndpub.data.dte.items.ItemDte;
import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.services.ItemsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemsService itemsService;
    
    @GetMapping()
    public List<ItemDte> getItems() {
        return this.itemsService.getItems().values().stream().toList();
    }

    @GetMapping("/{name}")
    public List<ItemDte> getItemOnName(@PathVariable("name") String name) {
        List<ItemDte> races = new ArrayList<>();
        for(ItemDte race: this.getItems()) {
            if(race.getName().toLowerCase().contains(name.toLowerCase())) {
                races.add(race);
            }
        }
        return races;
    }
}
