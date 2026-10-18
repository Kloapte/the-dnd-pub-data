package com.thedndpub.data.importer.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.item.ItemCopyResolver;
import com.thedndpub.data.dto.item.ItemDefinition;
import com.thedndpub.data.dto.item.syntax.ItemChargeSyntaxCategory;
import com.thedndpub.data.dto.item.syntax.ItemChargeSyntaxIndex;
import com.thedndpub.data.dto.item.syntax.ItemChargeSyntaxObservation;
import com.thedndpub.data.dto.item.syntax.ItemChargeSyntaxOwner;
import com.thedndpub.data.dte.item.ItemFileDte;
import com.thedndpub.data.mechanics.syntax.AuthoredSyntaxTextWalker;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.MarkupTag;
import com.thedndpub.data.mechanics.syntax.SyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxObservationClassifier;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxText;
import com.thedndpub.data.services.ItemCatalog;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/** Indexes bounded charge-adjacent syntax from original Item source strings. */
public final class ItemChargeSyntaxAdapter {
    private static final String RESOURCE_PATH = "data/items.json";
    private static final Pattern CHARGE_WORD = Pattern.compile(
            "\\b(?:charg(?:e[sd]?|ing)|recharg\\w*)\\b", Pattern.CASE_INSENSITIVE);

    private final ObjectMapper objectMapper;
    private final Resource resource;
    private final AuthoredSyntaxTextWalker textWalker = new AuthoredSyntaxTextWalker();
    private final BalancedMarkupParser parser = new BalancedMarkupParser();
    private final SyntaxObservationClassifier classifier = new SyntaxObservationClassifier();

    public ItemChargeSyntaxAdapter(ObjectMapper objectMapper) {
        this(objectMapper, new ClassPathResource(RESOURCE_PATH));
    }

    public ItemChargeSyntaxAdapter(ObjectMapper objectMapper, Resource resource) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
        this.resource = Objects.requireNonNull(resource, "resource");
    }

    public ItemChargeSyntaxIndex adapt(ItemCatalog itemCatalog) {
        Objects.requireNonNull(itemCatalog, "itemCatalog");
        try (var input = resource.getInputStream()) {
            byte[] sourceBytes = input.readAllBytes();
            ItemFileDte source = objectMapper.readerFor(ItemFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .<ItemFileDte>readValue(sourceBytes);
            JsonNode document = parseDocument(sourceBytes);
            String fingerprint = sha256(sourceBytes);
            return build(source, document, fingerprint, itemCatalog);
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not index original Item charge syntax", exception);
        }
    }

    private ItemChargeSyntaxIndex build(
            ItemFileDte source,
            JsonNode document,
            String fingerprint,
            ItemCatalog itemCatalog) {
        if (source.item() == null || source.item().isEmpty()) {
            throw new IllegalStateException("Nonempty Item source array is required");
        }
        JsonNode authoredItems = document.path("item");
        if (!authoredItems.isArray() || authoredItems.size() != source.item().size()) {
            throw new IllegalStateException("Typed and original Item root counts do not match");
        }

        List<ItemChargeSyntaxOwner> owners = new ArrayList<>();
        List<ItemChargeSyntaxObservation> observations = new ArrayList<>();
        for (int itemIndex = 0; itemIndex < source.item().size(); itemIndex++) {
            var authored = source.item().get(itemIndex);
            ItemDefinition definition = findDefinition(itemCatalog, authored.name(), authored.source());
            String ownerPointer = "/item/" + itemIndex;
            SyntaxSourceOwner evidenceOwner = new SyntaxSourceOwner(
                    RESOURCE_PATH, fingerprint, "item", ownerPointer);
            owners.add(new ItemChargeSyntaxOwner(
                    definition.id(), authored.name(), authored.source(), evidenceOwner));

            JsonNode authoredNode = authoredItems.get(itemIndex);
            boolean hasChargeDeclaration = hasChargeDeclaration(authoredNode);
            int[] occurrenceIndex = {0};
            textWalker.walk(authoredNode, evidenceOwner, text -> appendIfInScope(
                    definition.id(), ownerPointer, hasChargeDeclaration, text, occurrenceIndex, observations));
        }

        if (owners.size() != itemCatalog.itemRootCount()) {
            throw new IllegalStateException("Authored Item roots do not match the Item catalog");
        }
        return new ItemChargeSyntaxIndex(1, itemCatalog.size(), owners, observations);
    }

    private void appendIfInScope(
            String itemId,
            String ownerPointer,
            boolean hasChargeDeclaration,
            SyntaxText text,
            int[] occurrenceIndex,
            List<ItemChargeSyntaxObservation> output) {
        String pointer = text.location().jsonPointer();
        ItemChargeSyntaxCategory fieldCategory = fieldCategory(ownerPointer, pointer);
        boolean entryContent = isEntryContent(ownerPointer, pointer);
        boolean chargeMarkedEntry = entryContent && CHARGE_WORD.matcher(text.text()).find();
        boolean chargeOwnerEntry = entryContent && hasChargeDeclaration;
        if (fieldCategory == null && !chargeMarkedEntry && !chargeOwnerEntry) {
            return;
        }

        List<MarkupTag> tags;
        try {
            tags = parser.parse(text).preorder();
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Could not parse Item syntax at "
                    + text.location().owner().resourcePath() + "#" + pointer, exception);
        }

        for (MarkupTag tag : tags) {
            SyntaxObservation observation = classifier.classify(tag);
            if (fieldCategory == null && !chargeMarkedEntry
                    && !(chargeOwnerEntry && observation instanceof FormulaSyntaxObservation)) {
                continue;
            }
            ItemChargeSyntaxCategory category = category(fieldCategory, chargeMarkedEntry, observation);
            output.add(new ItemChargeSyntaxObservation(
                    itemId, occurrenceIndex[0]++, category, observation));
        }
    }

    private static ItemChargeSyntaxCategory fieldCategory(String ownerPointer, String pointer) {
        if (pointer.equals(ownerPointer + "/charges")) {
            return ItemChargeSyntaxCategory.COUNT_FIELD_SYNTAX;
        }
        if (pointer.equals(ownerPointer + "/rechargeAmount")) {
            return ItemChargeSyntaxCategory.RECOVERY_FIELD_SYNTAX;
        }
        return null;
    }

    private static ItemChargeSyntaxCategory category(
            ItemChargeSyntaxCategory fieldCategory,
            boolean chargeMarkedEntry,
            SyntaxObservation observation) {
        if (fieldCategory != null) {
            return fieldCategory;
        }
        if (observation instanceof FormulaSyntaxObservation) {
            return ItemChargeSyntaxCategory.CHARGE_OWNER_ENTRY_FORMULA;
        }
        if (chargeMarkedEntry) {
            return ItemChargeSyntaxCategory.CHARGE_MARKED_ENTRY_OTHER_SYNTAX;
        }
        throw new IllegalStateException("Selected Item syntax has no charge-related source context");
    }

    private static boolean hasChargeDeclaration(JsonNode item) {
        return item.has("charges") || item.has("recharge") || item.has("rechargeAmount");
    }

    private static boolean isEntryContent(String ownerPointer, String textPointer) {
        String ownerContent = textPointer.substring(ownerPointer.length());
        return ownerContent.contains("/entries/") || ownerContent.contains("/additionalEntries/");
    }

    private static ItemDefinition findDefinition(ItemCatalog itemCatalog, String name, String source) {
        List<ItemDefinition> matches = itemCatalog.findByName(name).stream()
                .filter(definition -> definition.source().identity().code().equals(source))
                .toList();
        if (matches.size() != 1) {
            throw new IllegalStateException("Authored Item must match exactly one effective catalog definition: "
                    + name + " | " + source);
        }
        return matches.getFirst();
    }

    private JsonNode parseDocument(byte[] sourceBytes) throws IOException {
        try (JsonParser parser = objectMapper.getFactory().createParser(sourceBytes)) {
            JsonNode document = objectMapper.readTree(parser);
            if (document == null || !document.isObject() || parser.nextToken() != null) {
                throw new IllegalStateException("Item resource must contain one JSON object");
            }
            return document;
        }
    }

    private static String sha256(byte[] sourceBytes) throws NoSuchAlgorithmException {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(sourceBytes));
    }
}
