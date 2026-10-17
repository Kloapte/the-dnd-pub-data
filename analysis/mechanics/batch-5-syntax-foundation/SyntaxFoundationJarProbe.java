import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;

/** Loads only packaged foundation classes, with no target/classes fallback. */
public final class SyntaxFoundationJarProbe {
    public static void main(String[] args) throws Exception {
        URL packagedClasses = URI.create("jar:" + Path.of(args[0]).toUri() + "!/BOOT-INF/classes/").toURL();
        String prefix = "com.thedndpub.data.mechanics.syntax.";
        try (URLClassLoader loader = new URLClassLoader(new URL[]{packagedClasses}, ClassLoader.getPlatformClassLoader())) {
            Class<?> ownerType = loader.loadClass(prefix + "SyntaxSourceOwner");
            Class<?> originType = loader.loadClass(prefix + "SyntaxOrigin");
            Class<?> locationType = loader.loadClass(prefix + "SyntaxTextLocation");
            Class<?> textType = loader.loadClass(prefix + "SyntaxText");
            Class<?> resultType = loader.loadClass(prefix + "MarkupParseResult");
            Class<?> parserType = loader.loadClass(prefix + "BalancedMarkupParser");
            Class<?> classifierType = loader.loadClass(prefix + "SyntaxObservationClassifier");
            Object owner = ownerType.getConstructor(String.class, String.class, String.class, String.class)
                    .newInstance("data/probe.json", null, "probe", "/probe/0");
            Object location = locationType.getConstructor(ownerType, String.class, List.class, originType)
                    .newInstance(owner, "/probe/0/entries/0", List.of(), originType.getEnumConstants()[0]);
            Object text = textType.getConstructor(locationType, String.class)
                    .newInstance(location, "😀 {@damage 8d6} then {@spell Shield|PHB}");
            Object parser = parserType.getConstructor().newInstance();
            Object parsed = parserType.getMethod("parse", textType).invoke(parser, text);
            List<?> tags = (List<?>) resultType.getMethod("preorder").invoke(parsed);
            Object classifier = classifierType.getConstructor().newInstance();
            List<?> observations = (List<?>) classifierType.getMethod("classify", resultType).invoke(classifier, parsed);
            if (tags.size() != 2 || !observations.getFirst().getClass().getSimpleName().equals("FormulaSyntaxObservation")
                    || !observations.getLast().getClass().getSimpleName().equals("OpaqueSyntaxObservation")) {
                throw new IllegalStateException("Packaged syntax parsing/classification failed");
            }
            Class<?> tagType = loader.loadClass(prefix + "MarkupTag");
            Object evidence = tagType.getMethod("evidence").invoke(tags.getFirst());
            Object span = evidence.getClass().getMethod("span").invoke(evidence);
            int start = (Integer) span.getClass().getMethod("startInclusive").invoke(span);
            if (start != 3) {
                throw new IllegalStateException("Packaged UTF-16 offset changed");
            }
            System.out.println("{\"packagedFoundationClasses\":true,\"parsing\":true,\"utf16Offsets\":true}");
        }
    }
}
