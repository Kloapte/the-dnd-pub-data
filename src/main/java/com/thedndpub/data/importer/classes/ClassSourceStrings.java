package com.thedndpub.data.importer.classes;

import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/** Traverses typed immutable source records without serializing them back into lossy JSON shapes. */
final class ClassSourceStrings {
    private ClassSourceStrings() {
    }

    static void visit(Object value, String path, BiConsumer<String, String> consumer) {
        if (value == null) {
            return;
        }
        if (value instanceof String text) {
            consumer.accept(path, text);
        } else if (value instanceof List<?> values) {
            for (int index = 0; index < values.size(); index++) {
                visit(values.get(index), path + "[" + index + "]", consumer);
            }
        } else if (value instanceof Map<?, ?> values) {
            values.forEach((key, child) -> visit(child, path + "." + key, consumer));
        } else if (value.getClass().isRecord()) {
            for (RecordComponent component : value.getClass().getRecordComponents()) {
                try {
                    visit(component.getAccessor().invoke(value), path + "." + component.getName(), consumer);
                } catch (ReflectiveOperationException exception) {
                    throw new IllegalStateException("Could not inspect typed class source at " + path, exception);
                }
            }
        }
    }
}
