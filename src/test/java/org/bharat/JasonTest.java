package org.bharat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("While testing Jason for json serialization, ")
class JasonTest {
    private Jason jason;

    @BeforeEach
    void setUp() {
        this.jason = new Jason();
    }

    @Test
    @DisplayName("Testing simple strings")
    void simpleStrings() {
        Jason<JsonObj> jason = new Jason<>();

        JsonObj obj = new JsonObj("bharat", "maheshwari", 19);

        final var actualJson = jason.serialize(JsonObj.class, obj);

        final var expectedJson = """
                {
                \t"firstname": "bharat",
                \t"lastname": "maheshwari",
                }
                """;

        assertEquals(expectedJson, actualJson);
    }
}