package org.bharat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class JasonDeserialize<T> {
    private char curChar;
    private int pos = 0;
    private String json;
    private final boolean isStrict;

    public JasonDeserialize() {
        this.isStrict = true;
    }

    public JasonDeserialize(final boolean isStrict) {
        this.isStrict = isStrict;
    }

    /**
     * Returns deserialized object of type {@code tClass} but returns {@code null} in case of error
     * @param json JSON string in valid format
     * @param tClass Type of deserialized object
     * @return POJO made by parsing {@code json}
     */
    public T deserialize(final String json, Class<T> tClass) {
        this.json = json;
        this.curChar = json.charAt(pos);

        final var obj = deserialize(tClass);

        if (obj == null) {
            // FIXME: Display all the errors and maybe return Optional<T> from this
            System.out.println("Some errors happened");
        }

        return tClass.cast(obj);
    }

    public Object deserialize(Class<?> tClass) {
        Map<String, Object> obj = new HashMap<>();

        final var fields = tClass.getDeclaredFields();
        this.skipTilValidChar();

        switch (this.curChar) {
            case '{' -> {
                this.nextChar(); // skip the {

                while (true) {

                    // skip till we encounter a valid char such as a double quote(")
                    skipTilValidChar();

                    if (this.curChar == '}') {
                        this.nextChar();

                        if (this.curChar == ',') {
                            this.nextChar();
                        }

                        break;
                    }

                    final String key = parseKey();

                    final var fieldOpt = Arrays.stream(fields)
                            .filter(f -> f.getName().equals(key))
                            .findFirst();

                    // if there isn't a field with that name simple parse ahead and skip it
                    if (fieldOpt.isEmpty()) {
                        deserialize(Object.class);
                        continue;
                    }

                    final var field = fieldOpt.get();

                    final Class<?> objClass = field.getType();

                    if (objClass.isAssignableFrom(List.class)) {
                        final var listParameter = (ParameterizedType) field.getGenericType();
                        final var listGenericClass = (Class<?>) Arrays.stream(listParameter.getActualTypeArguments()).findFirst().get();

                        obj.put(key, deserialize(listGenericClass));
                    } else { //simple strings and objects
                        obj.put(key, deserialize(objClass));
                    }
                }
            }
            case '"' -> {
                return parseKey();
            }
            case '[' -> {
                return parseArray(tClass);
            }
            default -> {
                if (Character.isDigit(this.curChar)) {
                    return parseNumber();
                } else {
                    return parseNull();
                }
            }
        }

        if (tClass == Object.class || tClass.isRecord()) {
            // if it's a record there will only be a single all args constructor
            // we don't have to check if the constructor exists since records will 100% have a all args constructor
            final var constructor = Arrays.stream(tClass.getDeclaredConstructors()).findFirst().get();

            if (isStrict && constructor.getParameterCount() > obj.size()) {
                throw new RuntimeException(String.format("Not enough fields to create the object. " +
                        "Number of required fields: %d, got %d%n", constructor.getParameterCount(), obj.size()));
            }

            final var argsList = Arrays.stream(constructor.getParameters())
                    .map(parameter -> {
                        if (Boolean.FALSE.equals(obj.containsKey(parameter.getName()))) {
                            System.out.printf("[WARN] Missing field [%s] in the given JSON string%n", parameter.getName());

                            return null;
                        }

                        return obj.get(parameter.getName());
                    }).toList();
            try {
                return constructor.newInstance(argsList.toArray());
            } catch (Exception e) {
                System.out.println("Error creating new instance of record: " + e.getMessage());
            }
        } else {
            try {
                final var constructor = tClass.getDeclaredConstructor();
                final var instance = constructor.newInstance();

                Arrays.stream(fields)
                        .filter(field -> {
                            if (obj.containsKey(field.getName())) {
                                return true;
                            }

                            System.out.printf("[WARN] No field named [%s] found on provided JSON%n", field.getName());
                            return false;
                        })
                        .forEach(field -> {
                            try {
                                final var setter = tClass.getDeclaredMethod(setterName(field.getName()), field.getType());

                                setter.invoke(instance, obj.get(field.getName()));
                            } catch (NoSuchMethodException e) {
                                System.out.printf("ERROR no setter found for field [%s]%n", field.getName());
                            } catch (InvocationTargetException e) {
                                System.out.println("ERROR innovation target exception: " + e.getMessage());
                            } catch (IllegalAccessException e) {
                                System.out.printf("ERROR accessing the function [%s] due to: %s%n", setterName(field.getName()),e.getMessage());
                            }
                        });

                return instance;
            } catch (NoSuchMethodException e) {
                System.out.printf("ERROR no default constructor found for class: [%s]%n%n", tClass);
                return null;
            } catch (InvocationTargetException e) {
                System.out.printf("ERROR invoking default constructor of class: [%s] due to %s%n", tClass, e.getMessage());
            } catch (InstantiationException e) {
                System.out.printf("ERROR creating instance of class: [%s] due to %s%n", tClass, e.getMessage());
            } catch (Exception e) {
                System.out.println("ERROR with constructor: " + e.getMessage());
            }
        }

        return null;
    }

    private Object parseNumber() {
        final var startPos = this.pos;
        boolean isDecimal = false;

        while (Character.isDigit(this.curChar) || this.curChar == '.') {
            if (this.curChar == '.') isDecimal = true;

            this.nextChar();
        }

        final var numStr = this.json.substring(startPos, this.pos);

        this.nextChar(); // skip comma or newline

        if (isDecimal) {
            return Double.parseDouble(numStr);
        } else {
            return Integer.parseInt(numStr);
        }
    }

    private Object parseNull() {
        final String NULL_STR = "null";

        for (int i = 0; i < NULL_STR.length(); i++) {
            if (this.curChar != NULL_STR.charAt(i)) {
                throw new RuntimeException("Invalid Null string at: " + this.pos + " -> [" + this.curChar + "]");
            }
            this.nextChar();
        }

        this.nextChar(); // skip comma or newline

        return null;
    }

    private String parseKey() {
        this.nextChar(); // skip the double quote

        final int startPos = this.pos;

        while (this.curChar != '"') {
            this.nextChar();
        }

        final var key = this.json.substring(startPos, pos);

        this.nextChar(); // skip the double quote "

        // skip the colon and space if it's a key otherwise just skip the comma
        if (curChar == ',' || curChar == '\n') {
            this.nextChar(); //skip , or \n
        } else {
            this.nextChar(); // skip :
            this.nextChar(); // skip whitespace
        }

        return key;
    }

    private Object parseArray(Class<?> aClass) {
        this.nextChar(); // skip the [

        List<Object> items = new ArrayList<>();

        while (this.curChar != ']') {
            items.add(deserialize(aClass));
            this.skipTilValidChar();
        }

        // this.skipTilValidChar();
        this.nextChar(); // skip the ]

        return items.stream().map(aClass::cast).toList();
    }

    private void nextChar() {
        if (++pos >= this.json.length()) {
            this.curChar = '\0';
            return;
        }

        this.curChar = this.json.charAt(pos);
    }

    private void skipTilValidChar() {
        while (Character.isWhitespace(curChar)) {
            this.nextChar();
        }
    }

    private String setterName(final String fieldName) {
        return "set" + Character.toUpperCase(fieldName.charAt(0)) +
                fieldName.substring(1);
    }
}