package org.bharat;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JasonDeserialize<T> {
    private char curChar;
    private int pos = 0;
    private String json;

    public T deserialize(final String json, Class<T> tClass) {
        this.json = json;
        this.curChar = json.charAt(pos);

        return (T) deserialize(tClass, 0);
    }

    public Object deserialize(Class<?> tClass, final int depth) {
        Map<String, Object> obj = new HashMap<>();

        final var fields = tClass.getDeclaredFields();

        switch (this.curChar) {
            case '{' -> {
                this.nextChar(); // skip the {

                while (true) {

                    // skip till we encounter a valid char such as a double quote(")
                    skipTilValidChar();

                    if (this.curChar == '}') {
                        this.nextChar();
                        break;
                    }

                    this.nextChar(); // skip the double quote
                    final String key = parseKey();

                    final Class<?> objClass = Arrays.stream(fields).filter(field -> field.getName().equals(key)).findFirst().get() // FIXME: Handle absence of field with that name here
                            .getType();

                    obj.put(key, deserialize(objClass, depth + 1));
                }
            }
            case '"' -> {
                this.nextChar(); // skip the quote FIXME: handle skipping it in parseKey() itself
                return parseKey();
            }
            default -> {
                throw new RuntimeException("Handle default case bro");
            }
        }

        if (tClass.isRecord()) {
            // if it's a record there will only be a single all args contructor
            final var constructor = Arrays.stream(tClass.getDeclaredConstructors()).findFirst().get();

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
        }

        return null;
    }

    private String parseKey() {
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