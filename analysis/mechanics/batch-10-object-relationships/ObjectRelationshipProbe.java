import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.object.ObjectDefinitionMapper;
import com.thedndpub.data.dto.object.ObjectDetailDto;
import com.thedndpub.data.dte.object.ObjectAttackDte;
import com.thedndpub.data.dte.object.ObjectFileDte;
import com.thedndpub.data.mechanics.syntax.AuthoredSyntaxTextWalker;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.ModifierSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxObservationClassifier;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

/** Analysis-only probe of existing typed mapping and syntax code; no relationship extractor. */
public final class ObjectRelationshipProbe {
    public static void main(String[] arguments) throws Exception {
        Path sourcePath = Path.of("src/main/resources/data/objects.json");
        byte[] bytes = Files.readAllBytes(sourcePath);
        String sourceHash = sha256(bytes);
        ObjectMapper mapper = new ObjectMapper()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        var document = mapper.readTree(bytes);
        var typed = mapper.readValue(bytes, ObjectFileDte.class);
        var definitionMapper = new ObjectDefinitionMapper();
        var walker = new AuthoredSyntaxTextWalker();
        var parser = new BalancedMarkupParser();
        var classifier = new SyntaxObservationClassifier();
        ObjectNode report = mapper.createObjectNode();
        report.put("sourceSha256", sourceHash);
        report.put("definitionCount", typed.object().size());
        ArrayNode definitions = report.putArray("definitions");
        ArrayNode tags = report.putArray("tags");
        int actionsPreserved = 0;
        int publicActionsOmitted = 0;
        int typedAttacks = 0;

        for (int index = 0; index < typed.object().size(); index++) {
            var source = typed.object().get(index);
            var normalized = definitionMapper.map(source);
            var originalActions = source.actionEntries() == null ? List.of() : source.actionEntries();
            if (!normalized.actions().equals(originalActions)) {
                throw new IllegalStateException("Actions changed during normalization: " + source.name());
            }
            actionsPreserved++;
            var publicJson = mapper.valueToTree(ObjectDetailDto.from(normalized));
            if (publicJson.has("actions") || publicJson.has("actionEntries")
                    || publicJson.has("entries") || publicJson.toString().contains("{@")) {
                throw new IllegalStateException("Unexpected public action content: " + source.name());
            }
            publicActionsOmitted++;
            ObjectNode definition = definitions.addObject();
            definition.put("id", normalized.id());
            definition.put("ownerPointer", "/object/" + index);
            definition.put("actionWrapperCount", normalized.actions().size());
            for (int actionIndex = 0; actionIndex < normalized.actions().size(); actionIndex++) {
                var action = normalized.actions().get(actionIndex);
                for (int contentIndex = 0; contentIndex < action.entries().size(); contentIndex++) {
                    if (action.entries().get(contentIndex) instanceof ObjectAttackDte attack) {
                        var original = document.at("/object/" + index + "/actionEntries/"
                                + actionIndex + "/entries/" + contentIndex);
                        if (!original.equals(mapper.valueToTree(attack))) {
                            throw new IllegalStateException("Typed attack does not reconstruct source structure");
                        }
                        typedAttacks++;
                    }
                }
            }

            SyntaxSourceOwner owner = new SyntaxSourceOwner(
                    "data/objects.json", sourceHash, "object", "/object/" + index);
            walker.walk(document.path("object").get(index), owner, text -> {
                for (var observation : classifier.classify(parser.parse(text))) {
                    var tag = observation.tag();
                    var span = tag.evidence().span();
                    if (!span.extract(text.text()).equals(tag.originalText())) {
                        throw new IllegalStateException("Source span does not reconstruct");
                    }
                    ObjectNode evidence = tags.addObject();
                    evidence.put("ownerPointer", owner.jsonPointer());
                    evidence.put("pointer", text.location().jsonPointer());
                    evidence.set("hierarchy", mapper.valueToTree(text.location().hierarchy()));
                    evidence.put("origin", text.location().origin().name());
                    evidence.put("textSha256", sha256(text.text().getBytes(StandardCharsets.UTF_8)));
                    evidence.put("textUtf16Length", text.text().length());
                    evidence.put("startInclusive", span.startInclusive());
                    evidence.put("endExclusive", span.endExclusive());
                    evidence.put("originalText", tag.originalText());
                    evidence.put("name", tag.name());
                    evidence.put("rawArgumentText", tag.rawArgumentText());
                    evidence.set("arguments", mapper.valueToTree(tag.arguments()));
                    evidence.put("observationType", observation.getClass().getSimpleName());
                    evidence.put("childTagCount", tag.children().size());
                    if (observation instanceof FormulaSyntaxObservation formula) {
                        evidence.set("expression", mapper.valueToTree(formula.expression()));
                    } else if (observation instanceof ModifierSyntaxObservation modifier) {
                        evidence.set("expression", mapper.valueToTree(modifier.expression()));
                    }
                }
            });
        }
        report.put("actionsPreserved", actionsPreserved);
        report.put("publicActionsOmitted", publicActionsOmitted);
        report.put("typedAttacksReconstructed", typedAttacks);
        Path output = Path.of("analysis/mechanics/batch-10-object-relationships/runtime-evidence.json");
        Files.writeString(output, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report) + "\n");
        System.out.println("Verified " + typedAttacks + " typed attacks; " + tags.size() + " original tag spans");
    }

    private static String sha256(byte[] value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value));
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
