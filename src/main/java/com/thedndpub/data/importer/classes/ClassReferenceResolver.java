package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dto.classes.ClassDefinitionIdentity;
import com.thedndpub.data.dto.classes.ClassDefinitionKind;
import com.thedndpub.data.dto.classes.ClassFeatureGraphEdge;
import com.thedndpub.data.dto.classes.ClassId;
import com.thedndpub.data.dto.classes.ClassReprintReference;
import com.thedndpub.data.dto.classes.ClassResolvedFeatureReference;
import com.thedndpub.data.dto.classes.SubclassId;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dte.classes.ClassBlockEntryDte;
import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dte.classes.ClassEntryDte;
import com.thedndpub.data.dte.classes.ClassFeatureDte;
import com.thedndpub.data.dte.classes.ClassFeatureUnlockDte;
import com.thedndpub.data.dte.classes.ClassOptionsEntryDte;
import com.thedndpub.data.dte.classes.ClassReferenceEntryDte;
import com.thedndpub.data.dte.classes.ClassRootDte;
import com.thedndpub.data.dte.classes.SubclassDte;
import com.thedndpub.data.dte.classes.SubclassFeatureDte;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Own-domain exact indexes and dependency validation used during one normalization run. */
public final class ClassReferenceResolver {
    private final Map<String, ResolvedClassRecord> identities = new TreeMap<>();
    private final Map<String, ResolvedClassRecord> subclassParents = new TreeMap<>();
    private final List<ClassFeatureGraphEdge> edges = new ArrayList<>();
    private int authoredReferenceCount;
    private int orphanCount;

    public ClassReferenceResolver(List<ResolvedClassRecord> records) {
        Set<String> ids = new HashSet<>();
        for (ResolvedClassRecord record : records) {
            var identity = ClassIdentities.identity(record.data());
            if (identities.putIfAbsent(identity.lookupKey(), record) != null
                    || !ids.add(ClassIdentities.id(identity))) {
                throw new IllegalStateException("Duplicate class identity or readable ID: " + identity);
            }
            if (record.data() instanceof SubclassDte subclass) {
                String parentKey = parentKey(subclass.className(), subclass.classSource(),
                        subclass.shortName(), subclass.source());
                if (subclassParents.putIfAbsent(parentKey, record) != null) {
                    throw new IllegalStateException("Ambiguous subclass short-name identity: " + parentKey);
                }
            }
        }
        for (ResolvedClassRecord record : records) {
            validateParent(record.data());
            visitReferences(record.authored().data(), record.authored().location().pointer(), false);
            visitReferences(record.data(), record.authored().location().pointer() + "$effective", true);
        }
        validateGraph(records);
    }

    public ClassResolvedFeatureReference feature(ClassDefinitionKind kind, String uid) {
        var identity = ClassIdentities.featureUid(kind, uid);
        ResolvedClassRecord target = identities.get(identity.lookupKey());
        if (target == null) {
            throw new IllegalStateException("Unresolved " + kind + " reference: " + uid);
        }
        var authoredIdentity = ClassIdentities.identity(target.data());
        return new ClassResolvedFeatureReference(kind, ClassIdentities.id(authoredIdentity), authoredIdentity);
    }

    public ClassId parentClass(String name, String source) {
        var identity = new ClassDefinitionIdentity(ClassDefinitionKind.CLASS, name,
                new SourceIdentity(source), null, null, null, null, null);
        ResolvedClassRecord parent = identities.get(identity.lookupKey());
        if (parent == null) {
            throw new IllegalStateException("Unresolved parent class: " + name + "|" + source);
        }
        return new ClassId(ClassIdentities.id(ClassIdentities.identity(parent.data())));
    }

    public SubclassId parentSubclass(String name, String source, String shortName, String subclassSource) {
        ResolvedClassRecord parent = subclassParents.get(parentKey(name, source, shortName, subclassSource));
        if (parent == null) {
            throw new IllegalStateException("Unresolved parent subclass: "
                    + parentKey(name, source, shortName, subclassSource));
        }
        return new SubclassId(ClassIdentities.id(ClassIdentities.identity(parent.data())));
    }

    public List<SubclassId> subclasses(ClassId parent) {
        return subclassParents.values().stream()
                .map(record -> (SubclassDte) record.data())
                .filter(subclass -> ClassIdentities.classId(subclass.className(), subclass.classSource()).equals(parent.value()))
                .map(subclass -> new SubclassId(ClassIdentities.id(ClassIdentities.identity(subclass))))
                .sorted(java.util.Comparator.comparing(SubclassId::value))
                .toList();
    }

    public ClassReprintReference reprint(ClassDefinitionKind kind, String uid) {
        String[] parts = uid.split("\\|", -1);
        ResolvedClassRecord target;
        if (kind == ClassDefinitionKind.CLASS && parts.length == 2) {
            target = identities.values().stream()
                    .filter(record -> record.data() instanceof ClassDte value
                            && value.name().equalsIgnoreCase(parts[0]) && value.source().equalsIgnoreCase(parts[1]))
                    .findFirst().orElse(null);
        } else if (kind == ClassDefinitionKind.SUBCLASS && parts.length == 4) {
            target = subclassParents.get(parentKey(parts[1], parts[2], parts[0], parts[3]));
        } else {
            throw new IllegalArgumentException("Invalid class reprint UID: " + uid);
        }
        if (target == null) {
            throw new IllegalStateException("Unresolved class reprint: " + uid);
        }
        var identity = ClassIdentities.identity(target.data());
        return new ClassReprintReference(kind, ClassIdentities.id(identity), identity);
    }

    public List<ClassFeatureGraphEdge> edges() {
        return List.copyOf(edges);
    }

    public int authoredReferenceCount() {
        return authoredReferenceCount;
    }

    public int orphanCount() {
        return orphanCount;
    }

    private void validateParent(ClassRootDte root) {
        switch (root) {
            case ClassDte ignored -> {
            }
            case SubclassDte value -> parentClass(value.className(), value.classSource());
            case ClassFeatureDte value -> parentClass(value.className(), value.classSource());
            case SubclassFeatureDte value -> {
                parentClass(value.className(), value.classSource());
                parentSubclass(value.className(), value.classSource(), value.subclassShortName(), value.subclassSource());
            }
        }
    }

    private void visitReferences(ClassRootDte root, String path, boolean effective) {
        String ownerId = ClassIdentities.id(ClassIdentities.identity(root));
        if (root instanceof ClassDte value) {
            int index = 0;
            for (ClassFeatureUnlockDte unlock : safe(value.classFeatures())) {
                add(ownerId, ClassDefinitionKind.CLASS_FEATURE, unlock.classFeature(),
                        path + "/classFeatures/" + index++, effective);
            }
        } else if (root instanceof SubclassDte value) {
            int index = 0;
            for (String uid : safe(value.subclassFeatures())) {
                add(ownerId, ClassDefinitionKind.SUBCLASS_FEATURE, uid,
                        path + "/subclassFeatures/" + index++, effective);
            }
        }
        visitEntries(ownerId, entries(root), path + "/entries", effective);
    }

    private void visitEntries(String ownerId, List<ClassEntryDte> entries, String path, boolean effective) {
        int index = 0;
        for (ClassEntryDte entry : safe(entries)) {
            String entryPath = path + "/" + index++;
            switch (entry) {
                case ClassReferenceEntryDte reference -> {
                    if (reference.classFeature() != null) {
                        add(ownerId, ClassDefinitionKind.CLASS_FEATURE, reference.classFeature(), entryPath, effective);
                    } else if (reference.subclassFeature() != null) {
                        add(ownerId, ClassDefinitionKind.SUBCLASS_FEATURE, reference.subclassFeature(), entryPath, effective);
                    }
                }
                case ClassOptionsEntryDte options -> visitEntries(ownerId, options.entries(), entryPath + "/entries", effective);
                case ClassBlockEntryDte block -> {
                    visitEntries(ownerId, block.entries(), entryPath + "/entries", effective);
                    visitEntries(ownerId, block.items(), entryPath + "/items", effective);
                }
                default -> {
                }
            }
        }
    }

    private void add(String ownerId, ClassDefinitionKind kind, String uid, String path, boolean effective) {
        ClassResolvedFeatureReference reference = feature(kind, uid);
        if (effective) {
            edges.add(new ClassFeatureGraphEdge(ownerId, reference, path, edges.size()));
        } else {
            authoredReferenceCount++;
        }
    }

    private void validateGraph(List<ResolvedClassRecord> records) {
        Map<String, List<String>> adjacency = new TreeMap<>();
        for (var edge : edges) {
            adjacency.computeIfAbsent(edge.ownerId(), ignored -> new ArrayList<>()).add(edge.target().targetId());
        }
        Set<String> completed = new HashSet<>();
        for (String node : adjacency.keySet()) {
            visitDependency(node, adjacency, new HashSet<>(), completed);
        }
        Set<String> reachable = new HashSet<>();
        for (var record : records) {
            if (record.data() instanceof ClassDte || record.data() instanceof SubclassDte) {
                reach(ClassIdentities.id(ClassIdentities.identity(record.data())), adjacency, reachable);
            }
        }
        orphanCount = (int) records.stream()
                .filter(record -> record.data() instanceof ClassFeatureDte || record.data() instanceof SubclassFeatureDte)
                .filter(record -> !reachable.contains(ClassIdentities.id(ClassIdentities.identity(record.data()))))
                .count();
    }

    private static void visitDependency(
            String node, Map<String, List<String>> adjacency, Set<String> active, Set<String> completed) {
        if (completed.contains(node)) {
            return;
        }
        if (!active.add(node)) {
            throw new IllegalStateException("Cyclic class feature dependency at " + node);
        }
        for (String target : adjacency.getOrDefault(node, List.of())) {
            visitDependency(target, adjacency, active, completed);
        }
        active.remove(node);
        completed.add(node);
    }

    private static void reach(String node, Map<String, List<String>> adjacency, Set<String> visited) {
        if (!visited.add(node)) {
            return;
        }
        for (String target : adjacency.getOrDefault(node, List.of())) {
            reach(target, adjacency, visited);
        }
    }

    static List<ClassEntryDte> entries(ClassRootDte root) {
        return switch (root) {
            case ClassFeatureDte value -> value.entries();
            case SubclassFeatureDte value -> value.entries();
            default -> List.of();
        };
    }

    static <T> List<T> safe(List<T> values) {
        return values == null ? List.of() : values;
    }

    private static String parentKey(String className, String classSource, String shortName, String source) {
        if (className == null || classSource == null || shortName == null || source == null) {
            throw new IllegalArgumentException("Incomplete subclass parent identity");
        }
        return String.join("\u0000", className, classSource, shortName, source).toLowerCase(Locale.ROOT);
    }
}
