package org.bharat;

import java.util.Arrays;

public class Jason<T> {
    private StringBuilder json;

    public Jason() {
        this.json = new StringBuilder("{\n");
    }

    public String serialize(Class<T> tclass, Object obj) {
        final var methods = tclass.getDeclaredMethods();

        Arrays.stream(tclass.getDeclaredFields())
                .filter(field -> field.getType() == String.class)
                .forEach(field -> {
                    var fieldName = field.getName();
                    var getter = Arrays.stream(methods)
                            .filter(method ->
                                method.getName().equals(getterName(fieldName)))
                            .findFirst();

                    if (getter.isEmpty() || getter.get().getReturnType() != String.class) {
                        return;
                    }

                    try {
                        var fieldVal = getter.get().invoke(obj);

                        final String formatted = String.format("""
                                \t"%s": "%s",
                                """, fieldName, fieldVal);

                        json.append(formatted);
                    } catch (Exception _) {}
                });

        json.append("}\n");

        return json.toString();
    }

    private String getterName(final String fieldName) {
        StringBuilder getter = new StringBuilder("get");

        getter.append(Character.toUpperCase(fieldName.charAt(0)));

        getter.append(fieldName.substring(1));

        return getter.toString();
    }
}
