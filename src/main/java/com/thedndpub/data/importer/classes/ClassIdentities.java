package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dto.classes.ClassDefinitionIdentity;
import com.thedndpub.data.dto.classes.ClassDefinitionKind;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dte.classes.ClassCopyDte;
import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dte.classes.ClassFeatureDte;
import com.thedndpub.data.dte.classes.ClassRootDte;
import com.thedndpub.data.dte.classes.SubclassDte;
import com.thedndpub.data.dte.classes.SubclassFeatureDte;

import java.text.Normalizer;
import java.util.Locale;

public final class ClassIdentities {
    private ClassIdentities() {
    }

    public static ClassDefinitionIdentity identity(ClassRootDte root) {
        return switch (root) {
            case ClassDte value -> new ClassDefinitionIdentity(ClassDefinitionKind.CLASS,
                    value.name(), source(value.source()), null, null, null, null, null);
            case SubclassDte value -> new ClassDefinitionIdentity(ClassDefinitionKind.SUBCLASS,
                    value.name(), source(value.source()), value.className(), source(value.classSource()),
                    null, null, null);
            case ClassFeatureDte value -> new ClassDefinitionIdentity(ClassDefinitionKind.CLASS_FEATURE,
                    value.name(), source(value.source()), value.className(), source(value.classSource()),
                    null, null, value.level());
            case SubclassFeatureDte value -> new ClassDefinitionIdentity(ClassDefinitionKind.SUBCLASS_FEATURE,
                    value.name(), source(value.source()), value.className(), source(value.classSource()),
                    value.subclassShortName(), source(value.subclassSource()), value.level());
        };
    }

    public static ClassDefinitionIdentity copyIdentity(ClassRootDte root, ClassCopyDte copy) {
        ClassDefinitionKind kind = identity(root).kind();
        return new ClassDefinitionIdentity(kind, copy.name(), source(copy.source()),
                copy.className(), source(copy.classSource()), copy.subclassShortName(),
                copy.subclassSource() == null ? null : source(copy.subclassSource()), copy.level());
    }

    public static ClassDefinitionIdentity featureUid(ClassDefinitionKind kind, String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Feature UID is missing");
        }
        String[] parts = uid.split("\\|", -1);
        boolean subclass = kind == ClassDefinitionKind.SUBCLASS_FEATURE;
        int required = subclass ? 6 : 4;
        if ((kind != ClassDefinitionKind.CLASS_FEATURE && !subclass)
                || parts.length < required || parts.length > required + 1) {
            throw new IllegalArgumentException("Invalid " + kind + " UID: " + uid);
        }
        String classSource = fallback(parts[2], "PHB");
        if (!subclass) {
            return new ClassDefinitionIdentity(kind, parts[0], source(fallback(at(parts, 4), classSource)),
                    parts[1], source(classSource), null, null, level(parts[3], uid));
        }
        String subclassSource = fallback(parts[4], "PHB");
        return new ClassDefinitionIdentity(kind, parts[0], source(fallback(at(parts, 6), subclassSource)),
                parts[1], source(classSource), parts[3], source(subclassSource), level(parts[5], uid));
    }

    public static String id(ClassDefinitionIdentity identity) {
        String source = slug(identity.source().code());
        String name = slug(identity.name());
        String parent = identity.classSource() == null ? null
                : slug(identity.classSource().code()) + ":" + slug(identity.className());
        return switch (identity.kind()) {
            case CLASS -> "class:" + source + ":" + name;
            case SUBCLASS -> "subclass:" + source + ":" + parent + ":" + name;
            case CLASS_FEATURE -> "class-feature:" + source + ":" + parent + ":" + identity.level() + ":" + name;
            case SUBCLASS_FEATURE -> "subclass-feature:" + source + ":" + parent + ":"
                    + slug(identity.subclassSource().code()) + ":" + slug(identity.subclassShortName())
                    + ":" + identity.level() + ":" + name;
        };
    }

    public static String classId(String name, String source) {
        return id(new ClassDefinitionIdentity(ClassDefinitionKind.CLASS,
                name, source(source), null, null, null, null, null));
    }

    private static String slug(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Identity cannot form a readable ID: " + value);
        }
        return slug;
    }

    private static SourceIdentity source(String value) {
        return new SourceIdentity(value);
    }

    private static String fallback(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }

    private static String at(String[] parts, int index) {
        return parts.length > index ? parts[index] : null;
    }

    private static int level(String value, String uid) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid feature level in UID: " + uid, exception);
        }
    }
}
