import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/** Loads the probe plus packaged application classes/resources without target/classes fallback. */
public final class ObjectMembershipJarProbe {
    public static void main(String[] arguments) throws Exception {
        Path artifact = Path.of(arguments[0]).toAbsolutePath();
        Path probeClasses = Path.of(arguments[1]).toAbsolutePath();
        Path report = Path.of(arguments[2]).toAbsolutePath();
        Path libraries = artifact.resolveSibling("batch-10a-packaged-libs");
        Files.createDirectories(libraries);
        List<URL> urls = new ArrayList<>();
        urls.add(probeClasses.toUri().toURL());
        urls.add(URI.create("jar:" + artifact.toUri() + "!/BOOT-INF/classes/").toURL());
        try (JarFile jar = new JarFile(artifact.toFile())) {
            var entries = jar.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (!entry.getName().startsWith("BOOT-INF/lib/") || !entry.getName().endsWith(".jar")) {
                    continue;
                }
                String filename = entry.getName().substring("BOOT-INF/lib/".length());
                if (filename.contains("/") || filename.contains("\\")) {
                    throw new IllegalArgumentException("Unexpected library path: " + filename);
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
            Class<?> probe = loader.loadClass("ObjectMembershipRuntimeProbe");
            probe.getMethod("main", String[].class).invoke(null, (Object) new String[]{report.toString()});
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }
}
