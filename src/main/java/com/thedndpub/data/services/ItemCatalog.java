package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.item.ItemCopyResolver;
import com.thedndpub.data.dto.item.ItemBaseItemReference;
import com.thedndpub.data.dto.item.ItemDefinition;
import com.thedndpub.data.dto.item.ItemDefinitionKind;
import com.thedndpub.data.dto.item.ItemImportProvenance;
import com.thedndpub.data.dto.item.ItemAuxiliaryData;
import com.thedndpub.data.dto.item.ItemMapper;
import com.thedndpub.data.dto.item.ItemSummaryDto;
import com.thedndpub.data.dto.item.ItemVariantMatchData;
import com.thedndpub.data.dto.item.ResolvedItemDte;
import com.thedndpub.data.dte.item.BaseItemsFileDte;
import com.thedndpub.data.dte.item.ItemFileDte;
import com.thedndpub.data.dte.item.ItemSourceDte;
import com.thedndpub.data.dte.item.ItemTypeDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public final class ItemCatalog {
    private static final String ITEMS_RESOURCE = "data/items.json";
    private static final String BASE_ITEMS_RESOURCE = "data/items-base.json";

    private final Map<String, ItemDefinition> byId;
    private final Map<String, List<ItemDefinition>> byName;
    private final List<ItemSummaryDto> summaries;
    private final List<ItemVariantMatchData> variantMatchData;
    private final Map<String, ItemImportProvenance> importProvenance;
    private final List<ItemBaseItemReference> authoredBaseItemReferences;
    private final ItemAuxiliaryData auxiliaryData;
    private final int itemRootCount;
    private final int baseItemRootCount;
    private final int expandedCopyCount;

    @Autowired
    public ItemCatalog(ObjectMapper objectMapper, ItemMapper mapper, ItemCopyResolver copyResolver,
                       SourceRegistry sourceRegistry) {
        this(load(objectMapper, ITEMS_RESOURCE, ItemFileDte.class),
                load(objectMapper, BASE_ITEMS_RESOURCE, BaseItemsFileDte.class), mapper, copyResolver, sourceRegistry);
    }

    public ItemCatalog(ItemFileDte items, BaseItemsFileDte baseItems, ItemMapper mapper,
                       ItemCopyResolver copyResolver, SourceRegistry sourceRegistry) {
        if (items.item() == null || baseItems.baseitem() == null || items.item().isEmpty()
                || baseItems.baseitem().isEmpty()) {
            throw new IllegalStateException("Item resources must contain item and baseitem definitions");
        }
        validateAuxiliarySources(baseItems, sourceRegistry);
        validateGroups(items.itemGroup(), sourceRegistry);
        Map<String, ItemTypeDte> typeDefinitions = resolveTypes(baseItems.itemType());
        List<ResolvedItemDte> resolvedItems = copyResolver.resolve(items.item());
        Map<String, ItemDefinition> ids = new TreeMap<>();
        Map<String, ItemDefinition> identities = new TreeMap<>();
        Map<String, List<ItemDefinition>> names = new TreeMap<>();
        Map<String, ItemImportProvenance> provenance = new TreeMap<>();
        List<ItemSummaryDto> summariesBuilder = new ArrayList<>();
        List<ItemVariantMatchData> matchDataBuilder = new ArrayList<>();
        Set<String> baseItemIdentities = new HashSet<>();
        for (ItemSourceDte baseItem : baseItems.baseitem()) {
            baseItemIdentities.add(ItemCopyResolver.identity(baseItem.name(), baseItem.source()));
        }

        for (ResolvedItemDte resolved : resolvedItems) {
            ItemSourceDte source = resolved.source();
            validateItemSources(source, sourceRegistry, baseItemIdentities);
            ItemDefinition definition = mapper.map(resolved, ItemDefinitionKind.ITEM, typeDefinitions);
            add(definition, ids, identities, names, summariesBuilder);
            matchDataBuilder.add(mapper.mapVariantMatchData(definition, resolved));
            var copy = source.copy();
            provenance.put(definition.id(), new ItemImportProvenance(source, resolved, ItemDefinitionKind.ITEM,
                    copy == null ? null : copy.name(), copy == null ? null : copy.source(), resolved.copyResolved()));
        }
        for (ItemSourceDte source : baseItems.baseitem()) {
            validateItemSources(source, sourceRegistry, baseItemIdentities);
            ResolvedItemDte resolved = new ResolvedItemDte(source, null, false, source.property() == null
                    ? List.of() : source.property(), source.entries() == null ? List.of() : source.entries());
            ItemDefinition definition = mapper.map(resolved, ItemDefinitionKind.BASE_ITEM, typeDefinitions);
            add(definition, ids, identities, names, summariesBuilder);
            matchDataBuilder.add(mapper.mapVariantMatchData(definition, resolved));
            provenance.put(definition.id(), new ItemImportProvenance(source, resolved,
                    ItemDefinitionKind.BASE_ITEM,
                    null, null, false));
        }

        summariesBuilder.sort(Comparator.comparing(ItemSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(ItemSummaryDto::id));
        Map<String, List<ItemDefinition>> immutableNames = new TreeMap<>();
        names.forEach((key, value) -> immutableNames.put(key, List.copyOf(value)));
        byId = Collections.unmodifiableMap(ids);
        byName = Collections.unmodifiableMap(immutableNames);
        summaries = List.copyOf(summariesBuilder);
        matchDataBuilder.sort(Comparator.comparing(ItemVariantMatchData::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(ItemVariantMatchData::source, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(ItemVariantMatchData::itemId));
        variantMatchData = List.copyOf(matchDataBuilder);
        importProvenance = Collections.unmodifiableMap(provenance);
        authoredBaseItemReferences = provenance.entrySet().stream()
                .filter(entry -> entry.getValue().sourceRecord().baseItem() != null
                        && !entry.getValue().sourceRecord().baseItem().isBlank())
                .map(entry -> new ItemBaseItemReference(
                        entry.getKey(), entry.getValue().sourceRecord().baseItem()))
                .sorted(Comparator.comparing(ItemBaseItemReference::ownerItemId))
                .toList();
        auxiliaryData = new ItemAuxiliaryData(items, baseItems);
        itemRootCount = items.item().size();
        baseItemRootCount = baseItems.baseitem().size();
        expandedCopyCount = resolvedItems.stream().filter(ResolvedItemDte::copyResolved).toList().size();
    }

    public List<ItemSummaryDto> summaries() {
        return summaries;
    }

    public int size() {
        return byId.size();
    }

    public int itemRootCount() {
        return itemRootCount;
    }

    public int baseItemRootCount() {
        return baseItemRootCount;
    }

    public int expandedCopyCount() {
        return expandedCopyCount;
    }

    public List<ItemVariantMatchData> variantMatchData() {
        return variantMatchData;
    }

    public ItemDefinition findById(String id) {
        return byId.get(id);
    }

    public List<ItemBaseItemReference> authoredBaseItemReferences() {
        return authoredBaseItemReferences;
    }

    public List<ItemDefinition> findByName(String name) {
        return byName.getOrDefault(normalize(name), List.of());
    }

    private static void add(ItemDefinition item, Map<String, ItemDefinition> ids,
                            Map<String, ItemDefinition> identities, Map<String, List<ItemDefinition>> names,
                            List<ItemSummaryDto> summaries) {
        if (ids.putIfAbsent(item.id(), item) != null) {
            throw new IllegalStateException("Duplicate item ID: " + item.id());
        }
        String identity = ItemCopyResolver.identity(item.name(), item.source().identity().code());
        if (identities.putIfAbsent(identity, item) != null) {
            throw new IllegalStateException("Duplicate item name/source: " + identity);
        }
        names.computeIfAbsent(normalize(item.name()), ignored -> new ArrayList<>()).add(item);
        summaries.add(ItemSummaryDto.from(item));
    }

    private static Map<String, ItemTypeDte> resolveTypes(List<ItemTypeDte> values) {
        Map<String, ItemTypeDte> sourceTypes = indexTypes(values);
        Map<String, ItemTypeDte> result = new TreeMap<>();
        for (ItemTypeDte value : values) {
            result.put(ItemCopyResolver.identity(value.abbreviation(), value.source()),
                    resolveType(value, sourceTypes, new HashSet<>()));
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, ItemTypeDte> indexTypes(List<ItemTypeDte> values) {
        Map<String, ItemTypeDte> result = new TreeMap<>();
        for (ItemTypeDte value : values) {
            if (value.abbreviation() == null || value.source() == null) {
                continue;
            }
            String key = ItemCopyResolver.identity(value.abbreviation(), value.source());
            if (result.putIfAbsent(key, value) != null) {
                throw new IllegalStateException("Duplicate item type abbreviation/source: " + key);
            }
        }
        return result;
    }

    private static ItemTypeDte resolveType(ItemTypeDte type, Map<String, ItemTypeDte> all, Set<String> visiting) {
        String key = ItemCopyResolver.identity(type.abbreviation(), type.source());
        if (!visiting.add(key)) {
            throw new IllegalStateException("Cyclic item type copy chain at " + key);
        }
        ItemTypeDte parent = null;
        if (type.copy() != null) {
            String parentKey = ItemCopyResolver.identity(type.copy().abbreviation(), type.copy().source());
            ItemTypeDte parentRoot = all.get(parentKey);
            if (parentRoot == null) {
                throw new IllegalStateException("Item type copy target not found: " + parentKey);
            }
            parent = resolveType(parentRoot, all, visiting);
        }
        visiting.remove(key);
        if (parent == null) {
            return type;
        }
        return new ItemTypeDte(type.name() == null ? parent.name() : type.name(), type.abbreviation(), type.source(),
                type.page() == null ? parent.page() : type.page(),
                type.reprintedAs() == null ? parent.reprintedAs() : type.reprintedAs(),
                type.entries() == null ? parent.entries() : type.entries(),
                type.basicRules2024() == null ? parent.basicRules2024() : type.basicRules2024(), type.copy());
    }

    private static void validateAuxiliarySources(BaseItemsFileDte source, SourceRegistry registry) {
        source.itemProperty().forEach(value -> {
            requireSource(registry, value.source());
            if (value.reprintedAs() != null) {
                value.reprintedAs().forEach(reprint -> validateUidSource(registry, reprint.uid()));
            }
        });
        source.itemType().forEach(value -> {
            requireSource(registry, value.source());
            if (value.reprintedAs() != null) {
                value.reprintedAs().forEach(reprint -> validateUidSource(registry, reprint.uid()));
            }
            if (value.copy() != null) {
                requireSource(registry, value.copy().source());
            }
        });
        source.itemTypeAdditionalEntries().forEach(value -> requireSource(registry, value.source()));
        source.itemEntry().forEach(value -> requireSource(registry, value.source()));
        source.itemMastery().forEach(value -> requireSource(registry, value.source()));
    }

    private static void validateItemSources(ItemSourceDte item, SourceRegistry registry, Set<String> baseItems) {
        requireSource(registry, item.source());
        validateSourceReferences(registry, item.additionalSources());
        validateSourceReferences(registry, item.otherSources());
        if (item.referenceSources() != null) {
            item.referenceSources().forEach(code -> requireSource(registry, code));
        }
        if (item.reprintedAs() != null) {
            item.reprintedAs().forEach(reprint -> validateUidSource(registry, reprint.uid()));
        }
        validateQualifiedSource(registry, item.type());
        validateQualifiedSource(registry, item.typeAlt());
        validateUidSource(registry, item.baseItem());
        if (item.baseItem() != null && !baseItems.contains(uidIdentity(item.baseItem()))) {
            throw new IllegalStateException("Item references missing base item: " + item.baseItem());
        }
        if (item.property() != null) {
            item.property().forEach(value -> validateUidSource(registry, value.uid()));
        }
        if (item.mastery() != null) {
            item.mastery().forEach(value -> validateUidSource(registry, value.uid()));
        }
        if (item.attachedSpells() != null) {
            var spells = item.attachedSpells();
            validateUidSources(registry, spells.direct());
            validateUidSources(registry, spells.other());
            validateUidSources(registry, spells.ritual());
            validateSchedule(registry, spells.will());
            validateSchedule(registry, spells.daily());
            validateSchedule(registry, spells.rest());
            validateSchedule(registry, spells.charges());
            validateSchedule(registry, spells.limited());
        }
        if (item.packContents() != null) {
            item.packContents().forEach(value -> validateUidSource(registry, value.item()));
        }
        if (item.containerCapacity() != null && item.containerCapacity().item() != null) {
            item.containerCapacity().item().forEach(value -> value.keySet()
                    .forEach(uid -> validateUidSource(registry, uid)));
        }
        validateUidSources(registry, item.classFeatures());
        validateUidSources(registry, item.optionalfeatures());
        validateUidSources(registry, item.seeAlsoVehicle());
        validateUidSources(registry, item.seeAlsoDeck());
        if (item.ammoType() != null) {
            validateUidSource(registry, item.ammoType());
        }
        if (item.copy() != null) {
            requireSource(registry, item.copy().source());
        }
        if (item.reqAttuneTags() != null) {
            item.reqAttuneTags().forEach(tag -> {
                validateUidSource(registry, tag.className());
                validateUidSource(registry, tag.background());
                validateUidSource(registry, tag.race());
            });
        }
    }

    private static void validateSourceReferences(SourceRegistry registry,
                                                  List<com.thedndpub.data.dte.item.ItemSourceLocatorDte> values) {
        if (values != null) {
            values.forEach(value -> requireSource(registry, value.source()));
        }
    }

    private static void validateGroups(List<ItemSourceDte> groups, SourceRegistry registry) {
        groups.forEach(group -> {
            requireSource(registry, group.source());
            validateSourceReferences(registry, group.additionalSources());
            validateSourceReferences(registry, group.otherSources());
            if (group.referenceSources() != null) {
                group.referenceSources().forEach(code -> requireSource(registry, code));
            }
            if (group.reprintedAs() != null) {
                group.reprintedAs().forEach(value -> validateUidSource(registry, value.uid()));
            }
            validateUidSources(registry, group.items());
            validateUidSources(registry, group.seeAlsoVehicle());
            validateUidSources(registry, group.seeAlsoDeck());
        });
    }

    private static void validateUidSources(SourceRegistry registry, List<String> values) {
        if (values != null) {
            values.forEach(value -> validateUidSource(registry, value));
        }
    }

    private static void validateSchedule(
            SourceRegistry registry, com.thedndpub.data.dte.item.ItemSpellGrantDte value) {
        if (value == null) {
            return;
        }
        validateUidSources(registry, value.direct());
        value.byUses().values().forEach(spells -> validateUidSources(registry, spells));
    }

    private static void validateUidSource(SourceRegistry registry, String uid) {
        if (uid == null) {
            return;
        }
        String[] parts = uid.split("\\|", -1);
        if (parts.length > 1 && parts[parts.length - 1].matches("[A-Za-z][A-Za-z0-9.-]+")) {
            requireSource(registry, parts[parts.length - 1]);
        }
    }

    private static void validateQualifiedSource(SourceRegistry registry, String value) {
        if (value != null && value.contains("|")) {
            validateUidSource(registry, value);
        }
    }

    private static String uidIdentity(String uid) {
        String[] parts = uid.split("\\|", -1);
        if (parts.length != 2 || parts[1].isBlank()) {
            throw new IllegalStateException("Invalid source-qualified item reference: " + uid);
        }
        return ItemCopyResolver.identity(parts[0], parts[1]);
    }

    private static void requireSource(SourceRegistry registry, String code) {
        if (code == null || registry.find(code) == null) {
            throw new IllegalStateException("Source code is missing from Source Registry: " + code);
        }
    }

    private static <T> T load(ObjectMapper mapper, String path, Class<T> type) {
        try (InputStream input = new ClassPathResource(path).getInputStream()) {
            return mapper.readerFor(type).with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load item resource " + path, exception);
        }
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT);
    }
}
