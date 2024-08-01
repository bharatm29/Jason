package org.bharat;

import org.bharat.dummyObjs.JsonBigObj;
import org.bharat.dummyObjs.JsonBigObjArr;
import org.bharat.dummyObjs.JsonDummyObj;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        InputStream in = Main.class.getResourceAsStream("/big.json");

        final var json = Main.readFromInputStream(in);

        JasonDeserialize<JsonBigObjArr> jason = new JasonDeserialize<>();

        final var deserializedObj = jason.deserialize(json, JsonBigObjArr.class);

        Jason jasonSerialize = new Jason();
        final var serialize = jasonSerialize.serialize(deserializedObj);

        try (PrintWriter writer = new PrintWriter("serialize.json")) {
            writer.print(serialize);
        }
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