package org.bharat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.bharat.dummyObjs.JsonDummyObj;

public class Main {
    public static void main(String[] args) throws IOException {
        InputStream in = Main.class.getResourceAsStream("/test.json");

        final var json = Main.readFromInputStream(in);

        JasonDeserialize<JsonDummyObj> jason = new JasonDeserialize<>();

        final var deserializedObj = jason.deserialize(json, JsonDummyObj.class);

        System.out.println(deserializedObj);
    }

    private static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }

        inputStream.close();

        return resultStringBuilder.toString();
    }
}