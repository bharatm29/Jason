package org.bharat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class Jason {
    private boolean isRecord;
    final String INDENT_TOKEN = "  ";

    public Jason() {
        this.isRecord = true;
    }

    public String serialize(Object obj) {
        return this.serialize(obj, 0, "");
    }

    public String serialize(Object obj, final boolean isRecord) {
        this.isRecord = isRecord;
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
    private String serialize(Object obj, final int depth, String optionalObjName) {
        assert optionalObjName != null;

        StringBuilder json = new StringBuilder();

        json.append(INDENT_TOKEN.repeat(Math.max(0, depth)));
        if (!optionalObjName.isEmpty()) {
            json.append(String.format("\"%s\": ", optionalObjName));
        }
        json.append("{\n");

        var objClass = obj.getClass();

        if (this.isPrimitive(objClass)) {
            final String indent = INDENT_TOKEN.repeat(Math.max(0, depth));
            final String newline = depth > 0 ? "" : "\n";
            if (obj instanceof String) {
                return String.format("%s\"%s\"%s", indent, obj, newline);
            }

            return indent + obj + newline;
        }

        final var methods = objClass.getDeclaredMethods();
        final var fields = objClass.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            final Field field = fields[i];
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
                final String comma = (i + 1 == fields.length) ? "" : ",";

                var fieldVal = getterOpt.get().invoke(obj);

                final String indent = INDENT_TOKEN.repeat(Math.max(0, depth + 1));
                if (fieldVal == null) {
                    json.append("""
                            %s"%s": %s%s
                            """.formatted(indent, fieldName, "null", comma));
                } else if (isPrimitive(fieldVal.getClass())) {
                    String formatter = """
                            %s"%s": %s%s
                            """;

                    if (fieldVal instanceof String) {
                        formatter = """
                                %s"%s": "%s"%s
                                """;
                    }

                    final String formatted = String.format(formatter, indent, fieldName, fieldVal, comma);

                    json.append(formatted);
                } else if (fieldVal instanceof List<?> val) {
                    json.append(serializedArray(fieldName, val, depth + 1, comma));
                } else {
                    json.append(serialize(fieldVal, depth + 1, fieldName)).append(comma).append("\n");
                }
            } catch (Exception e) {
                System.out.println("Error when serializing field value: " + e.getMessage());
            }
        }

        json.append(INDENT_TOKEN.repeat(depth)).append("}");
        if (depth == 0) {
            json.append("\n");
        }

        return json.toString();
    }

    private String serializedArray(final String fieldName, final List<?> fieldVal, final int depth, final String comma) {
        final String indent = INDENT_TOKEN.repeat(Math.max(0, depth));
        StringBuilder json = new StringBuilder(String.format("""
                %s"%s": [
                """, indent, fieldName));

        fieldVal.forEach(obj -> {
            final String afterComma = obj == fieldVal.getLast() ? "" : ",";
            json.append(this.serialize(obj, depth + 1, ""))
                    .append(afterComma).append("\n");
        });

        json.append(indent).append("]").append(comma).append("\n");
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