package org.bharat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Jason {
    private final boolean isRecord;
    private Object currObj;

    public Jason() {
        this.isRecord = true;
    }

    public Jason(final boolean isRecord) {
        this.isRecord = isRecord;
    }

    public String serialize(Object obj) {
        return this.serialize(obj, 0, "");
    }

    /**
     * <p>Serializes object into json
     * </p>
     *
     * @param obj             Object to serialize
     * @param depth           indent depth
     * @param optionalObjName Object names in case of nested object. Pass empty string("") not null.
     * @return Serialized json
     * @since 1.0
     */
    public String serialize(Object obj, final int depth, String optionalObjName) {
        assert optionalObjName != null;
        this.currObj = obj;

        StringBuilder json = new StringBuilder();

        json.append("\t".repeat(Math.max(0, depth)));
        if (!optionalObjName.isEmpty()) {
            json.append(String.format("\"%s\": ", optionalObjName));
        }
        json.append("{\n");

        var objClass = obj.getClass();

        if (this.isPrimitive(objClass)) {
            // FIXME: Maybe remove indent concatenation frmo serializeArray and handle here?
            final String indent = "\t".repeat(Math.max(0, 0));
            if (obj instanceof String) {
                return String.format("%s\"%s\",\n", indent, obj);
            }

            return indent + obj + ",\n";
        }

        final var methods = objClass.getDeclaredMethods();

        for (final Field field : objClass.getDeclaredFields()) {
            var fieldName = field.getName();
            var getterOpt = Arrays.stream(methods)
                    .filter(method ->
                            method.getName().equals(getterName(fieldName)))
                    .findFirst();

            // FIXME: Provide valid errors for missing getters or misleading types
            if (getterOpt.isEmpty() || getterOpt.get().getReturnType() != field.getType()) {
                continue;
            }

            // FIXME: Handle null values for fields in every case
            try {
                var fieldVal = getterOpt.get().invoke(currObj);
                if (fieldVal == null) {
                    continue;
                }

                if (isPrimitive(fieldVal.getClass())) {
                    final String indent = "\t".repeat(Math.max(0, depth + 1));
                    String formatter = """
                            %s"%s": %s,
                            """;

                    if (fieldVal instanceof String) {
                        formatter = """
                                %s"%s": "%s",
                                """;
                    }

                    final String formatted = String.format(formatter, indent, fieldName, fieldVal);

                    json.append(formatted);
                } else if (fieldVal instanceof List<?> val) {
                    json.append(serializedArray(fieldName, val, depth + 1));
                } else {
                    json.append(serialize(fieldVal, depth + 1, fieldName));
                }
            } catch (Exception _) {
            }
        }

        json.append("\t".repeat(depth));
        json.append("}\n");

        return json.toString();
    }

    private String serializedArray(final String fieldName, final List<?> fieldVal, final int depth) {
        final String indent = "\t".repeat(Math.max(0, depth));
        StringBuilder json = new StringBuilder(String.format("""
                %s"%s": [
                """, indent, fieldName));

        fieldVal.forEach(obj -> {
            json.append("\t".repeat(Math.max(0, depth + 1)));

            json.append(this.serialize(obj, depth + 1, ""));
        });

        json.append(indent).append("],\n");
        return json.toString();
    }

    private String getterName(final String fieldName) {
        if (this.isRecord) {
            return fieldName;
        }

        return "get" + Character.toUpperCase(fieldName.charAt(0)) +
                fieldName.substring(1);
    }

    private boolean isPrimitive(Class<?> c) {
        return c == Integer.class || c == Double.class || c == Float.class || c == String.class || c == Boolean.class;
    }
}