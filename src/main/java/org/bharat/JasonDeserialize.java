package org.bharat;

import java.lang.reflect.ParameterizedType;
import java.util.*;

public class JasonDeserialize<T> {
    private char curChar;
    private int pos = 0;
    private String json;

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
                throw new RuntimeException("Handle default case: " + curChar);
            }
        }

        // FIXME: Handle constructor for class intances
        if (tClass == Object.class || tClass.isRecord()) {
            // if it's a record there will only be a single all args contructor
            final var constructor = Arrays.stream(tClass.getDeclaredConstructors()).findFirst().get();

            if (constructor.getParameterCount() > obj.size()) {
                throw new RuntimeException(String.format("Not enough fields to create the object. " +
                        "Number of required fields: %d, got %d%n", constructor.getParameterCount(), obj.size()));
            }

            final var argsList = Arrays.stream(constructor.getParameters())
                    .map(parameter -> {
                        // FIXME: Handle if the key doesn't exist for the parameter in the map
                        return obj.get(parameter.getName());
                    }).toList();
            try {
                return constructor.newInstance(argsList.toArray());
            } catch (Exception e) {
                System.out.println("Error creating new instance of record: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Class objects not handled: " + tClass);
        }

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
}