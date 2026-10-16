package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dto.classes.ClassDefinitionKind;
import com.thedndpub.data.dto.classes.ClassExternalFeatureNode;
import com.thedndpub.data.dto.classes.ClassExternalKind;
import com.thedndpub.data.dto.classes.ClassFeatureAbilityDeclaration;
import com.thedndpub.data.dto.classes.ClassFeatureChoice;
import com.thedndpub.data.dto.classes.ClassFeatureNode;
import com.thedndpub.data.dto.classes.ClassFeatureReferenceNode;
import com.thedndpub.data.dto.classes.ClassFeatureSection;
import com.thedndpub.data.dto.classes.ClassFeatureStatblock;
import com.thedndpub.data.dto.classes.ClassFeatureTable;
import com.thedndpub.data.dto.classes.ClassFeatureText;
import com.thedndpub.data.dto.classes.ClassTableRow;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.classes.ClassAbilityEntryDte;
import com.thedndpub.data.dte.classes.ClassBlockEntryDte;
import com.thedndpub.data.dte.classes.ClassEntryDte;
import com.thedndpub.data.dte.classes.ClassOptionsEntryDte;
import com.thedndpub.data.dte.classes.ClassReferenceEntryDte;
import com.thedndpub.data.dte.classes.ClassStatblockEntryDte;
import com.thedndpub.data.dte.classes.ClassTableEntryDte;
import com.thedndpub.data.dte.classes.ClassTextEntryDte;

import java.util.ArrayList;
import java.util.List;

import static com.thedndpub.data.importer.classes.ClassReferenceResolver.safe;

final class ClassFeatureContentNormalizer {
    private final ClassReferenceResolver features;
    private final ClassExternalReferenceResolver external;

    ClassFeatureContentNormalizer(ClassReferenceResolver features, ClassExternalReferenceResolver external) {
        this.features = features;
        this.external = external;
    }

    List<ClassFeatureNode> normalize(List<ClassEntryDte> entries) {
        List<ClassFeatureNode> result = new ArrayList<>();
        for (ClassEntryDte entry : safe(entries)) {
            result.add(switch (entry) {
                case ClassTextEntryDte value -> new ClassFeatureText(value.text());
                case ClassBlockEntryDte value -> new ClassFeatureSection(value.type(), value.name(), value.entry(),
                        normalize(value.entries()), normalize(value.items()));
                case ClassOptionsEntryDte value -> new ClassFeatureChoice(value.count(), value.style(), normalize(value.entries()));
                case ClassReferenceEntryDte value -> reference(value);
                case ClassAbilityEntryDte value -> new ClassFeatureAbilityDeclaration(value.type(), value.name(), value.attributes());
                case ClassStatblockEntryDte value -> new ClassFeatureStatblock(
                        external.resolve(value.tag().equals("item") ? ClassExternalKind.ITEM : ClassExternalKind.MONSTER,
                                value.name() + "|" + value.source()),
                        new SourceReference(value.source(), value.page()), value.style());
                case ClassTableEntryDte value -> new ClassFeatureTable(value.caption(), value.colLabels(), value.colStyles(),
                        safe(value.rows()).stream().map(row -> new ClassTableRow(null,
                                row.stream().map(ClassMechanicsNormalizer::cell).toList())).toList(), value.footnotes());
            });
        }
        return List.copyOf(result);
    }

    private ClassFeatureNode reference(ClassReferenceEntryDte reference) {
        if (reference.classFeature() != null) {
            return new ClassFeatureReferenceNode(features.feature(ClassDefinitionKind.CLASS_FEATURE, reference.classFeature()));
        }
        if (reference.subclassFeature() != null) {
            return new ClassFeatureReferenceNode(features.feature(ClassDefinitionKind.SUBCLASS_FEATURE, reference.subclassFeature()));
        }
        ClassExternalKind kind = reference.feat() == null ? ClassExternalKind.OPTIONAL_FEATURE : ClassExternalKind.FEAT;
        String uid = reference.feat() == null ? reference.optionalfeature() : reference.feat();
        return new ClassExternalFeatureNode(external.resolve(kind, uid), reference.name(),
                reference.data() == null ? null : reference.data().isRequiredOption(),
                reference.preserve() == null ? null : reference.preserve().consumes(),
                reference.preserve() == null ? null : reference.preserve().prerequisite());
    }
}
