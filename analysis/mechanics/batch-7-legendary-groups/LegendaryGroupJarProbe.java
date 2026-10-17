import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

/** Starts the packaged application in isolation, with no target/classes or source-directory fallback. */
public final class LegendaryGroupJarProbe {
    public static void main(String[] arguments) throws Exception {
        Path artifact = Path.of(arguments[0]).toAbsolutePath();
        Path libraries = artifact.resolveSibling("batch-7-packaged-libs");
        Files.createDirectories(libraries);
        URL classes = URI.create("jar:" + artifact.toUri() + "!/BOOT-INF/classes/").toURL();
        List<URL> urls = new ArrayList<>(List.of(classes));
        try (JarFile jar = new JarFile(artifact.toFile())) {
            var entries = jar.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (!entry.getName().startsWith("BOOT-INF/lib/") || !entry.getName().endsWith(".jar")) {
                    continue;
                }
                String filename = entry.getName().substring("BOOT-INF/lib/".length());
                if (filename.contains("/") || filename.contains("\\")) {
                    throw new IllegalArgumentException("Unexpected nested library path: " + filename);
                }
                Path target = libraries.resolve(filename);
                try (var input = jar.getInputStream(entry)) {
                    Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
                }
                urls.add(target.toUri().toURL());
            }
        }
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader loader = new URLClassLoader(urls.toArray(URL[]::new),
                ClassLoader.getPlatformClassLoader())) {
            Thread.currentThread().setContextClassLoader(loader);
            String prefix = "com.thedndpub.data.";
            Class<?> applicationType = loader.loadClass("org.springframework.boot.SpringApplication");
            Class<?> source = loader.loadClass(prefix + "DndPubDataApplication");
            Object application = applicationType.getConstructor(Class[].class)
                    .newInstance((Object) new Class<?>[]{source});
            Object context = applicationType.getMethod("run", String[].class).invoke(application, (Object) new String[]{
                    "--server.port=0", "--server.address=127.0.0.1", "--spring.main.banner-mode=off"
            });
            try (AutoCloseable closeable = (AutoCloseable) context) {
                Class<?> contextType = loader.loadClass("org.springframework.context.ApplicationContext");
                Class<?> catalogType = loader.loadClass(prefix + "services.LegendaryGroupSupportCatalog");
                Object catalog = contextType.getMethod("getBean", Class.class).invoke(context, catalogType);
                List<?> definitions = (List<?>) catalogType.getMethod("definitions").invoke(catalog);
                Map<?, ?> associations = (Map<?, ?>) catalogType.getMethod("associations").invoke(catalog);
                URL resource = loader.getResource("data/monsters/legendarygroups.json");
                if (definitions.size() != 209 || associations.size() != 307
                        || resource == null || !resource.toString().startsWith("jar:")) {
                    throw new IllegalStateException("Packaged legendary-group coverage/resource check failed");
                }
                if (!catalogType.getProtectionDomain().getCodeSource().getLocation().equals(classes)) {
                    throw new IllegalStateException("Support catalog did not load from the packaged classes");
                }
                System.out.println("{\"packagedSupportGroups\":209,\"monsterAssociations\":307,"
                        + "\"packagedResources\":true}");
            }
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }
}
