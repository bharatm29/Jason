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
        final var actualJson = jason.
                addString("first_name", "Bharat").
                addString("last_name", "Maheshwari").
                serialize();

        final var expectedJson = """
                {
                \t"first_name": "Bharat",
                \t"last_name": "Maheshwari",
                }
                """;

        assertEquals(expectedJson, actualJson);
    }
}