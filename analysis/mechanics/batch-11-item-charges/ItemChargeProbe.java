import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.DndPubDataApplication;
import com.thedndpub.data.dto.item.ItemCopyResolver;
import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.dto.item.ResolvedItemDte;
import com.thedndpub.data.dte.item.ItemFileDte;
import com.thedndpub.data.dte.item.ItemNumberTextDte;
import com.thedndpub.data.dte.item.ItemSourceDte;
import com.thedndpub.data.mechanics.syntax.AuthoredSyntaxTextWalker;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxObservationClassifier;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.services.ItemCatalog;
import org.springframework.boot.SpringApplication;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/** Analysis-only source syntax and runtime projection audit; no charge interpretation. */
public final class ItemChargeProbe {
    public static void main(String[] arguments) throws Exception {
        try (var context = SpringApplication.run(DndPubDataApplication.class,
                "--server.port=0", "--server.address=127.0.0.1", "--spring.main.banner-mode=off")) {
            ObjectMapper mapper = context.getBean(ObjectMapper.class).copy()
                    .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            ItemCatalog catalog = context.getBean(ItemCatalog.class);
            Path directory = Path.of("analysis/mechanics/batch-11-item-charges");
            byte[] bytes = Files.readAllBytes(Path.of("src/main/resources/data/items.json"));
            var file = mapper.readerFor(ItemFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).<ItemFileDte>readValue(bytes);
            JsonNode original = mapper.readTree(bytes);
            var pointers = new IdentityHashMap<ItemSourceDte, String>();
            for (int index = 0; index < file.item().size(); index++) {
                pointers.put(file.item().get(index), "/item/" + index);
            }
            List<ResolvedItemDte> resolved = new ItemCopyResolver().resolve(file.item());
            ObjectNode report = mapper.createObjectNode();
            report.put("effectiveDefinitionCount", catalog.size());
            report.put("itemRoots", catalog.itemRootCount());
            report.put("baseItemRoots", catalog.baseItemRootCount());
            report.put("resolvedCopyCount", catalog.expandedCopyCount());
            report.put("sourceSha256", digest(bytes));
            ArrayNode effective = report.putArray("effectiveItems");
            for (var item : resolved) {
                var definition = catalog.findByName(item.source().name()).stream()
                        .filter(value -> value.source().identity().code().equals(item.source().source()))
                        .findFirst().orElseThrow();
                ObjectNode row = effective.addObject();
                row.put("id", definition.id());
                row.put("name", definition.name());
                row.put("source", definition.source().identity().code());
                row.put("ownerPointer", pointers.get(item.source()));
                row.put("copy", item.copyResolved());
                row.put("effectiveEntryCount", item.resolvedEntries().size());
                row.put("effectiveEntrySha256", digest(mapper.writeValueAsBytes(item.resolvedEntries())));
                row.set("normalizedCharges", mapper.valueToTree(definition.charges()));
                row.set("publicCharges", mapper.valueToTree(ItemDto.from(definition).charges()));
                row.set("normalizedAttachedSpells", mapper.valueToTree(definition.attachedSpells()));
                var fields = row.putObject("effectiveFields");
                appendField(mapper, fields, item, "charges", ItemSourceDte::charges, pointers);
                appendField(mapper, fields, item, "recharge", ItemSourceDte::recharge, pointers);
                appendField(mapper, fields, item, "rechargeAmount", ItemSourceDte::rechargeAmount, pointers);
                List<String> lineage = new ArrayList<>();
                for (var ancestor = item.copiedFrom(); ancestor != null; ancestor = ancestor.copiedFrom()) {
                    lineage.add(pointers.get(ancestor.source()));
                }
                row.set("copyLineage", mapper.valueToTree(lineage));
            }

            ArrayNode tags = report.putArray("authoredTags");
            Map<String, Integer> families = new TreeMap<>();
            var parser = new BalancedMarkupParser();
            var classifier = new SyntaxObservationClassifier();
            for (int index = 0; index < file.item().size(); index++) {
                var owner = new SyntaxSourceOwner("data/items.json", digest(bytes), "item", "/item/" + index);
                new AuthoredSyntaxTextWalker().walk(original.path("item").get(index), owner, text -> {
                    for (var observation : classifier.classify(parser.parse(text))) {
                        var tag = observation.tag();
                        if (!tag.evidence().span().extract(text.text()).equals(tag.originalText())) {
                            throw new IllegalStateException("Original span does not reconstruct");
                        }
                        families.merge(tag.name(), 1, Integer::sum);
                        boolean chargeLeaf = text.text().matches("(?is).*\\b(?:charges?|recharg\\w*)\\b.*");
                        boolean dedicatedField = text.location().jsonPointer().equals(owner.jsonPointer() + "/charges")
                                || text.location().jsonPointer().equals(owner.jsonPointer() + "/rechargeAmount");
                        if (!(observation instanceof FormulaSyntaxObservation) && !chargeLeaf && !dedicatedField) {
                            continue;
                        }
                        ObjectNode row = tags.addObject();
                        row.put("ownerPointer", owner.jsonPointer());
                        row.put("pointer", text.location().jsonPointer());
                        row.put("origin", text.location().origin().name());
                        row.put("textSha256", digest(text.text().getBytes(StandardCharsets.UTF_8)));
                        row.put("startInclusive", tag.evidence().span().startInclusive());
                        row.put("endExclusive", tag.evidence().span().endExclusive());
                        row.put("name", tag.name());
                        row.put("originalText", tag.originalText());
                        row.set("arguments", mapper.valueToTree(tag.arguments()));
                        row.put("observationType", observation.getClass().getSimpleName());
                        if (observation instanceof FormulaSyntaxObservation formula) {
                            row.set("expression", mapper.valueToTree(formula.expression()));
                        }
                    }
                });
            }
            report.set("allAuthoredTagFamilies", mapper.valueToTree(families));
            report.put("allAuthoredTagsParsed", families.values().stream().mapToInt(Integer::intValue).sum());
            Files.writeString(directory.resolve("runtime-evidence.json"),
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report) + "\n");
            System.out.println("Audited " + resolved.size() + " effective Items; retained " + tags.size()
                    + " candidate syntax observations without interpreting charge mechanics");
        }
    }

    private static <T> void appendField(
            ObjectMapper mapper,
            ObjectNode fields,
            ResolvedItemDte item,
            String field,
            Function<ItemSourceDte, T> getter,
            IdentityHashMap<ItemSourceDte, String> pointers) {
        for (var owner = item; owner != null; owner = owner.copiedFrom()) {
            T value = getter.apply(owner.source());
            if (value == null) {
                continue;
            }
            var node = fields.putObject(field);
            node.put("originPointer", pointers.get(owner.source()) + "/" + field);
            node.put("inherited", owner != item);
            node.set("typedValue", mapper.valueToTree(value));
            return;
        }
    }

    private static String digest(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
