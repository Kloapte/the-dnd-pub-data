package com.thedndpub.data.services;

import com.thedndpub.data.dto.item.ItemDefinition;
import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.dto.item.ItemSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class ItemsService {
    private final ItemCatalog catalog;
    private final PublicReferenceProjection references;

    public ItemsService(ItemCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<ItemSummaryDto> find(String name, String source, String type, String rarity) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        String normalizedType = normalize(type);
        String normalizedRarity = normalize(rarity);
        return catalog.summaries().stream()
                .filter(item -> normalizedName == null || item.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(item -> normalizedSource == null
                        || item.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .filter(item -> normalizedType == null || item.typeCodes().stream()
                        .anyMatch(code -> matchesType(code, normalizedType)))
                .filter(item -> normalizedRarity == null || item.rarity() != null
                        && item.rarity().toLowerCase(Locale.ROOT).equals(normalizedRarity))
                .toList();
    }

    public ItemDto byId(String id) {
        ItemDefinition item = catalog.findById(id);
        if (item == null) {
            throw new ItemNotFoundException("No item found with ID '" + id + "'");
        }
        return ItemDto.from(item).withCrossCatalogReferences(references.forOwner("item", id));
    }

    public List<ItemDto> byName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        String normalizedSource = normalize(source);
        List<ItemDto> matches = catalog.findByName(name).stream()
                .filter(item -> normalizedSource == null || item.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(normalizedSource))
                .map(item -> ItemDto.from(item).withCrossCatalogReferences(
                        references.forOwner("item", item.id())))
                .toList();
        if (matches.isEmpty()) {
            throw new ItemNotFoundException("No item found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean matchesType(String code, String requestedType) {
        String normalizedCode = code.toLowerCase(Locale.ROOT);
        if (normalizedCode.equals(requestedType)) {
            return true;
        }
        int qualifier = normalizedCode.indexOf('|');
        return qualifier > 0 && normalizedCode.substring(0, qualifier).equals(requestedType);
    }
}
