package org.bharat;

import org.bharat.jsonObjs.JsonStr;
import org.bharat.jsonObjs.JsonStrNumBool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("While testing Jason for json serialization, ")
class JasonTest {

    @Test
    @DisplayName("Testing simple strings")
    void simpleStrings() {
        Jason<JsonStr> jason = new Jason<>();
        JsonStr obj = new JsonStr("bharat", "maheshwari");

        final var actualJson = jason.serialize(JsonStr.class, obj);

        final var expectedJson = """
                {
                \t"firstname": "bharat",
                \t"lastname": "maheshwari",
                }
                """;

        assertEquals(expectedJson, actualJson);
    }

    @Test
    @DisplayName("Testing strings, numbers and booleans")
    void strNumsBooleans() {
        Jason<JsonStrNumBool> jason = new Jason<>();
        JsonStrNumBool obj = new JsonStrNumBool("bharat maheshwari", 19, 100.29, false);

        final var actualJson = jason.serialize(JsonStrNumBool.class, obj);

        final var expectedJson = """
                {
                \t"name": "bharat maheshwari",
                \t"age": 19,
                \t"price": 100.29,
                \t"isValid": false,
                }
                """;

        assertEquals(expectedJson, actualJson);
    }
}