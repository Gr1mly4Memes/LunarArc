package io.lunararcdevs.lunararc.neoforge.mod;

import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.fml.loading.moddiscovery.readers.JarModsDotTomlModFileReader;
import net.neoforged.neoforgespi.locating.IModFile;
import net.neoforged.neoforgespi.locating.IModFileReader;
import net.neoforged.neoforgespi.locating.IOrderedProvider;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public final class LunarArcModFileReader_NeoForge implements IModFileReader {
    private static final Logger LOGGER = LoggerFactory.getLogger("LunarArc");
    private static final String SERVICES = "META-INF/services/";
    private final Set<String> libraryPackages = collectLibraryPackages();
    private final Set<String> reported = ConcurrentHashMap.newKeySet();
    private final IModFileReader delegate = new JarModsDotTomlModFileReader();

    private static Set<String> collectLibraryPackages() {
        Set<String> packages = new HashSet<>();
        for (String name : LunarArcModFileReader_NeoForge.class.getModule().getPackages()) {
            if (!name.startsWith("io.lunararcdevs.") && !name.startsWith("META-INF")) packages.add(name);
        }
        return packages;
    }

    private static boolean ownedByLunarArc(Path path) {
        return path.toString().replace('\\', '/').contains("/.lunararc/");
    }

    private static String stripLeadingSlash(String entry) {
        return entry.startsWith("/") ? entry.substring(1) : entry;
    }

    private static String packageOf(String entry) {
        String normalized = stripLeadingSlash(entry);
        int slash = normalized.lastIndexOf('/');
        return slash < 0 ? "" : normalized.substring(0, slash).replace('/', '.');
    }

    private static String packageOfClass(String className) {
        return className.substring(0, Math.max(0, className.lastIndexOf('.')));
    }

    private static String describe(Path primary, ModFileDiscoveryAttributes attributes) {
        Path name = primary.getFileName();
        if (name != null && !name.toString().isEmpty()) return name.toString();
        return attributes.parent() == null ? primary.toString() : "a library nested in " + attributes.parent().getFileName();
    }

    @Override
    public int getPriority() {
        return IOrderedProvider.HIGHEST_SYSTEM_PRIORITY;
    }

    @Override
    public IModFile read(JarContents jar, ModFileDiscoveryAttributes attributes) {
        Path primary = jar.getPrimaryPath();
        if (primary == null || ownedByLunarArc(primary)) return null;

        Set<String> clashing = new TreeSet<>();
        Set<String> staleServiceFiles = new TreeSet<>();
        scan(jar, clashing, staleServiceFiles);
        if (clashing.isEmpty()) return null;

        IModFile modFile = readWithoutClashingEntries(primary, attributes, clashing, staleServiceFiles);
        if (modFile == null) return null;
        String description = describe(primary, attributes);
        String examples = String.join(", ", clashing.stream().limit(3).toList());
        if (reported.add(description + ": " + examples)) {
            LOGGER.info("{} declares {} package(s) LunarArc already provides ({}); hiding them so it uses LunarArc's copy", description, clashing.size(), examples);
        }
        return modFile;
    }

    /** Collects the packages (and the service files naming them) that clash with LunarArc's own libraries. */
    private void scan(JarContents jar, Set<String> clashing, Set<String> staleServiceFiles) {
        jar.visitContent((entry, resource) -> {
            String name = stripLeadingSlash(entry);
            if (name.endsWith(".class")) {
                String pkg = packageOf(name);
                if (libraryPackages.contains(pkg)) clashing.add(pkg);
            } else if (name.startsWith(SERVICES) && name.length() > SERVICES.length()) {
                try (BufferedReader reader = resource.bufferedReader(StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int comment = line.indexOf('#');
                        String implementation = (comment < 0 ? line : line.substring(0, comment)).trim();
                        if (implementation.isEmpty()) continue;
                        String implementationPackage = packageOfClass(implementation);
                        if (libraryPackages.contains(implementationPackage)) {
                            staleServiceFiles.add(name);
                            clashing.add(implementationPackage);
                            break;
                        }
                    }
                } catch (IOException unreadable) {
                    LOGGER.debug("Could not read service file {}", name, unreadable);
                }
            }
        });
    }

    private IModFile readWithoutClashingEntries(Path primary, ModFileDiscoveryAttributes attributes, Set<String> clashing, Set<String> staleServiceFiles) {
        try {
            if (!Files.isRegularFile(primary)) return null;
            JarContents filtered = JarContents.ofFilteredPaths(Collections.singletonList(new JarContents.FilteredPath(primary,
                    entry -> !clashing.contains(packageOf(entry)) && !staleServiceFiles.contains(stripLeadingSlash(entry)))));
            IModFile modFile = delegate.read(filtered, attributes);
            if (modFile == null) filtered.close();
            return modFile;
        } catch (Throwable failure) {
            LOGGER.warn("Could not reopen {} without the packages that clash with LunarArc's own", primary.getFileName(), failure);
            return null;
        }
    }

    @Override
    public String toString() {
        return "lunararc-duplicate-libraries";
    }
}
