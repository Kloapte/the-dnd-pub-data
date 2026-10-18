import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.DndPubDataApplication;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.services.ObjectCatalog;
import com.thedndpub.data.services.ObjectsService;
import org.springframework.boot.SpringApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/** Baseline/packaged compatibility probe; does not publish source data in its report. */
public final class ObjectMembershipRuntimeProbe {
    public static void main(String[] arguments) throws Exception {
        try (var context = SpringApplication.run(DndPubDataApplication.class,
                "--server.port=0", "--server.address=127.0.0.1", "--spring.main.banner-mode=off")) {
            ObjectMapper mapper = context.getBean(ObjectMapper.class);
            var catalog = context.getBean(ObjectCatalog.class);
            var service = context.getBean(ObjectsService.class);
            var references = context.getBean(CrossCatalogResolutionCoordinator.class).snapshot().results();
            var publicValues = catalog.definitions().stream().map(value -> service.byId(value.id())).toList();
            Map<String, Object> report = new LinkedHashMap<>();
            report.put("objectCount", catalog.size());
            report.put("objectIdsSha256", digest(mapper.writeValueAsBytes(
                    catalog.definitions().stream().map(value -> value.id()).toList())));
            report.put("objectDefinitionsSha256", digest(mapper.writeValueAsBytes(catalog.definitions())));
            report.put("objectPublicJsonSha256", digest(mapper.writeValueAsBytes(publicValues)));
            report.put("resolverCount", references.size());
            report.put("resolverSha256", digest(mapper.writeValueAsBytes(references)));
            Map<String, Long> outcomes = new java.util.TreeMap<>();
            references.forEach(value -> outcomes.merge(value.outcome().name(), 1L, Long::sum));
            report.put("resolverOutcomes", outcomes);
            if (context.containsBean("objectEvidenceMembershipCatalog")) {
                Object bean = context.getBean("objectEvidenceMembershipCatalog");
                Object index = bean.getClass().getMethod("index").invoke(bean);
                Object memberships = index.getClass().getMethod("memberships").invoke(index);
                report.put("membershipCount", ((java.util.List<?>) memberships).size());
                report.put("membershipSha256", digest(mapper.writeValueAsBytes(memberships)));
            }
            Files.writeString(Path.of(arguments[0]), mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(report) + "\n");
        }
    }

    private static String digest(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }
}
