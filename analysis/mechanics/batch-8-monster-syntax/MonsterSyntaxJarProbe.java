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

/** Starts the packaged application in isolation and reads the internal Monster syntax index. */
public final class MonsterSyntaxJarProbe {
    public static void main(String[] arguments) throws Exception {
        Path artifact = Path.of(arguments[0]).toAbsolutePath();
        Path libraries = artifact.resolveSibling("batch-8-packaged-libs");
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
            Class<?> springApplication = loader.loadClass("org.springframework.boot.SpringApplication");
            Class<?> applicationType = loader.loadClass(prefix + "DndPubDataApplication");
            Object application = springApplication.getConstructor(Class[].class)
                    .newInstance((Object) new Class<?>[]{applicationType});
            Object context = springApplication.getMethod("run", String[].class).invoke(application,
                    (Object) new String[]{"--server.port=0", "--server.address=127.0.0.1",
                            "--spring.main.banner-mode=off"});

            try (AutoCloseable closeable = (AutoCloseable) context) {
                Class<?> contextType = loader.loadClass("org.springframework.context.ApplicationContext");
                Class<?> syntaxCatalogType = loader.loadClass(
                        prefix + "services.MonsterSyntaxAnnotationCatalog");
                Class<?> monsterCatalogType = loader.loadClass(prefix + "services.MonsterCatalog");
                Class<?> supportCatalogType = loader.loadClass(prefix + "services.LegendaryGroupSupportCatalog");

                Object syntaxCatalog = contextType.getMethod("getBean", Class.class)
                        .invoke(context, syntaxCatalogType);
                Object monsterCatalog = contextType.getMethod("getBean", Class.class)
                        .invoke(context, monsterCatalogType);
                Object supportCatalog = contextType.getMethod("getBean", Class.class)
                        .invoke(context, supportCatalogType);
                Object index = syntaxCatalogType.getMethod("index").invoke(syntaxCatalog);

                int sourceRoots = (int) index.getClass().getMethod("authoredMonsterRootCount").invoke(index);
                int effectiveMonsters = (int) index.getClass().getMethod("effectiveMonsterCount").invoke(index);
                int observations = ((List<?>) index.getClass().getMethod("annotations").invoke(index)).size();
                int resources = (int) index.getClass().getMethod("sourceFileCount").invoke(index);
                int catalogMonsters = (int) monsterCatalogType.getMethod("size").invoke(monsterCatalog);
                int supportGroups = ((Map<?, ?>) supportCatalogType.getMethod("definitionsById")
                        .invoke(supportCatalog)).size();
                int associations = ((Map<?, ?>) supportCatalogType.getMethod("associations")
                        .invoke(supportCatalog)).size();
                URL monsterResource = loader.getResource("data/monsters/bestiary-mm.json");

                if (sourceRoots != 4_560 || effectiveMonsters != 4_851 || observations != 57_534
                        || resources != 111 || catalogMonsters != 4_851 || supportGroups != 209
                        || associations != 307 || monsterResource == null
                        || !monsterResource.toString().startsWith("jar:")) {
                    throw new IllegalStateException("Packaged Monster syntax coverage check failed");
                }
                if (!syntaxCatalogType.getProtectionDomain().getCodeSource().getLocation().equals(classes)) {
                    throw new IllegalStateException("Monster syntax catalog did not load from packaged classes");
                }
                System.out.println("{\"authoredMonsterRoots\":4560,\"effectiveMonsters\":4851,"
                        + "\"syntaxObservations\":57534,\"sourceFiles\":111,"
                        + "\"legendaryGroups\":209,\"associations\":307,\"packagedResources\":true}");
            }
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }
}
