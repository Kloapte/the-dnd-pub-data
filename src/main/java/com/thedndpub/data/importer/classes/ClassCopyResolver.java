package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dte.classes.ClassCopyDte;
import com.thedndpub.data.dte.classes.ClassCopyPreserveDte;
import com.thedndpub.data.dte.classes.ClassRootDte;
import com.thedndpub.data.dte.classes.SubclassDte;
import com.thedndpub.data.dte.classes.SubclassFeatureDte;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Class-specific fill-missing copy semantics; authored nulls and lists override atomically. */
public final class ClassCopyResolver {
    private static final Set<String> PROTECTED_METADATA = Set.of(
            "page", "otherSources", "referenceSources", "reprintedAs", "srd", "srd52",
            "basicRules", "basicRules2024", "edition", "isReprinted", "hasFluff", "hasFluffImages");

    public List<ResolvedClassRecord> resolve(ClassSourceCorpus corpus) {
        Map<String, ClassSourceRecord> roots = new TreeMap<>();
        for (ClassSourceRecord root : corpus.records()) {
            String identity = ClassIdentities.identity(root.data()).lookupKey();
            if (roots.putIfAbsent(identity, root) != null) {
                throw new IllegalStateException("Duplicate class definition identity at " + root.location().pointer());
            }
        }
        Map<String, ResolvedClassRecord> resolved = new TreeMap<>();
        for (String identity : roots.keySet()) {
            resolve(identity, roots, resolved, new HashSet<>());
        }
        return List.copyOf(resolved.values());
    }

    private ResolvedClassRecord resolve(
            String identity,
            Map<String, ClassSourceRecord> roots,
            Map<String, ResolvedClassRecord> resolved,
            Set<String> active) {
        if (resolved.containsKey(identity)) {
            return resolved.get(identity);
        }
        ClassSourceRecord destination = roots.get(identity);
        if (destination == null) {
            throw new IllegalStateException("Missing class copy base: " + identity);
        }
        if (!active.add(identity)) {
            throw new IllegalStateException("Cyclic class copy dependency at " + destination.location().pointer());
        }
        ClassCopyDte copy = copy(destination.data());
        ResolvedClassRecord result;
        if (copy == null) {
            result = new ResolvedClassRecord(destination.data(), destination, List.of(), Set.of(), Set.of());
        } else {
            String baseIdentity = ClassIdentities.copyIdentity(destination.data(), copy).lookupKey();
            ResolvedClassRecord base = resolve(baseIdentity, roots, resolved, active);
            if (destination.data() instanceof SubclassDte && base.data() instanceof SubclassDte subclass
                    && (copy.shortName() == null || !copy.shortName().equalsIgnoreCase(subclass.shortName()))) {
                throw new IllegalStateException("Copy short name contradicts its target at "
                        + destination.location().pointer());
            }
            if (!ClassIdentities.identity(destination.data()).className()
                    .equalsIgnoreCase(ClassIdentities.identity(base.data()).className())) {
                throw new IllegalStateException("A class copy cannot change parent class name");
            }
            Set<String> inherited = new HashSet<>();
            Set<String> suppressed = new HashSet<>();
            ClassRootDte merged = merge(base.data(), destination, copy.preserve(), inherited, suppressed);
            if (!ClassIdentities.identity(merged).lookupKey().equals(identity)) {
                throw new IllegalStateException("Copy changed destination identity");
            }
            List<ClassSourceLocation> lineage = new ArrayList<>(base.copyBases());
            lineage.add(base.authored().location());
            result = new ResolvedClassRecord(merged, destination, lineage, inherited, suppressed);
        }
        active.remove(identity);
        resolved.put(identity, result);
        return result;
    }

    private static ClassRootDte merge(
            ClassRootDte base,
            ClassSourceRecord destination,
            ClassCopyPreserveDte preserve,
            Set<String> inherited,
            Set<String> suppressed) {
        try {
            RecordComponent[] components = destination.data().getClass().getRecordComponents();
            Class<?>[] types = new Class<?>[components.length];
            Object[] arguments = new Object[components.length];
            for (int index = 0; index < components.length; index++) {
                RecordComponent component = components[index];
                String field = component.getName().equals("copy") ? "_copy" : component.getName();
                types[index] = component.getType();
                if (destination.fields().present().contains(field)) {
                    arguments[index] = component.getAccessor().invoke(destination.data());
                } else if (PROTECTED_METADATA.contains(field) && !preserved(preserve, field)) {
                    suppressed.add(field);
                } else {
                    arguments[index] = component.getAccessor().invoke(base);
                    if (arguments[index] != null) {
                        inherited.add(field);
                    }
                }
            }
            return (ClassRootDte) destination.data().getClass().getDeclaredConstructor(types).newInstance(arguments);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not construct typed class copy", exception);
        }
    }

    private static boolean preserved(ClassCopyPreserveDte preserve, String field) {
        if (preserve == null) {
            return false;
        }
        Boolean value = switch (field) {
            case "page" -> preserve.page();
            case "otherSources" -> preserve.otherSources();
            case "referenceSources" -> preserve.referenceSources();
            case "srd" -> preserve.srd();
            case "basicRules" -> preserve.basicRules();
            case "reprintedAs" -> preserve.reprintedAs();
            default -> false;
        };
        return Boolean.TRUE.equals(value);
    }

    public static ClassCopyDte copy(ClassRootDte root) {
        return switch (root) {
            case SubclassDte value -> value.copy();
            case SubclassFeatureDte value -> value.copy();
            default -> null;
        };
    }
}
