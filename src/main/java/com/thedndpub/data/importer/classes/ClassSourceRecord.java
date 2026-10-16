package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dte.classes.ClassRootDte;

public record ClassSourceRecord(
        ClassRootDte data,
        ClassSourceLocation location,
        ClassFieldPresence fields) {
}
