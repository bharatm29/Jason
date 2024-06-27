package org.bharat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Jason {
    private StringBuilder json;

    private final boolean isRecord;
    private Object currObj;

    public Jason() {
        this.isRecord = true;
        this.json = new StringBuilder("{\n");
    }

    public Jason(final boolean isRecord) {
        this.isRecord = isRecord;
        this.json = new StringBuilder("{\n");
    }

    /**
     * <p>Serializes object into json
     * </p>
     * @param obj Object to serialize
     * @return Serialized json
     * @since 1.0
     */
    public String serialize(Object obj) {
        this.currObj = obj;

        var objClass = obj.getClass();
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

            if (field.getType() == String.class) {
                serializeString(fieldName, getterOpt.get());
            } else if (field.getType() == Integer.class || field.getType() == Double.class) {
                serializeNumber(fieldName, getterOpt.get());
            } else if (field.getType() == Boolean.class) {
                serializeBoolean(fieldName, getterOpt.get());
            }
        }

        json.append("}\n");

        final String jsonStr = json.toString();

        // reset for next use
        json = new StringBuilder("{\n");

        return jsonStr;
    }

    private void serializeString(final String fieldName, final Method getter) {
        try {
            var fieldVal = getter.invoke(currObj);

            final String formatted = String.format("""
                    \t"%s": "%s",
                    """, fieldName, fieldVal);

            json.append(formatted);
        } catch (Exception _) {
        }
    }

    private void serializeNumber(final String fieldName, final Method getter) {
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

    private void serializeBoolean(final String fieldName, final Method getter) {
        try {
            var fieldVal = getter.invoke(currObj);

            final String formatted = String.format("""
                    \t"%s": %s,
                    """, fieldName, fieldVal);

            json.append(formatted);
        } catch (Exception _) {
        }
    }

    private String getterName(final String fieldName) {
        if (this.isRecord) {
            return fieldName;
        }

        return "get" + Character.toUpperCase(fieldName.charAt(0)) +
                fieldName.substring(1);
    }
}