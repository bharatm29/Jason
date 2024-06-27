package org.bharat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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
        return this.serialize(obj, 0);
    }

    /**
     * <p>Serializes object into json
     * </p>
     *
     * @param obj Object to serialize
     * @param depth indent depth
     * @return Serialized json
     * @since 1.0
     */
    public String serialize(Object obj, final int depth) {
        this.currObj = obj;

        StringBuilder json = new StringBuilder("{\n");
        var objClass = obj.getClass();

        if (this.isPrimitive(objClass)) {
            return objClass == String.class ? String.format("\"%s\",\n", obj) : obj + ",\n";
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

            // FIXME: Handle null values for fields in everycase
            if (field.getType() == String.class) {
                serializeString(fieldName, getterOpt.get(), json);
            } else if (field.getType() == Integer.class || field.getType() == Double.class) {
                serializeNumber(fieldName, getterOpt.get(), json);
            } else if (field.getType() == Boolean.class) {
                serializeBoolean(fieldName, getterOpt.get(), json);
            } else if (field.getType() == List.class) {
                json.append(serializedArray(fieldName, getterOpt.get(), depth + 1));
            }
        }

        json.append("}\n");

        return json.toString();
    }

    private void serializeString(final String fieldName, final Method getter, StringBuilder json) {
        try {
            var fieldVal = getter.invoke(currObj);

            final String formatted = String.format("""
                    \t"%s": "%s",
                    """, fieldName, fieldVal);

            json.append(formatted);
        } catch (Exception _) {
        }
    }

    private void serializeNumber(final String fieldName, final Method getter, StringBuilder json) {
        try {
            var fieldVal = getter.invoke(currObj);

            String numberStr = fieldVal.toString();

            final String formatted = String.format("""
                    \t"%s": %s,
                    """, fieldName, numberStr);

            json.append(formatted);
        } catch (Exception _) {
        }
    }

    private void serializeBoolean(final String fieldName, final Method getter, StringBuilder json) {
        try {
            var fieldVal = getter.invoke(currObj);

            final String formatted = String.format("""
                    \t"%s": %s,
                    """, fieldName, fieldVal);

            json.append(formatted);
        } catch (Exception _) {
        }
    }

    private String serializedArray(final String fieldName, final Method getter, final int depth) {
        final String indent = "\t".repeat(Math.max(0, depth));
        StringBuilder json = new StringBuilder(String.format("""
                %s"%s": [
                """, indent, fieldName));

        try {
            var fieldVal = (List<Object>) getter.invoke(currObj);

            fieldVal.forEach(obj -> {
                json.append("\t".repeat(Math.max(0, depth + 1)));

                json.append(this.serialize(obj, depth + 1));
            });
        } catch (Exception _) {
        }

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