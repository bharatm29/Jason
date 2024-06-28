package org.bharat;

import org.bharat.jsonObjs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("While testing Jason for json serialization, ")
class JasonTest {
    private Jason jason;

    @BeforeEach
    void setUp() {
        this.jason = new Jason();
    }

    @Test
    @DisplayName("Testing primitive")
    void testingPrimitives() {
        // These are not valid on their own but are used in (say) array of numbers
        assertEquals("1\n", jason.serialize(1));

        assertEquals("1.29\n", jason.serialize(1.29));

        assertEquals("true\n", jason.serialize(true));

        var expected = """
            "Name"
            """;
        assertEquals(expected, jason.serialize("Name"));
    }

    @Test
    @DisplayName("Testing simple strings")
    void testingSimpleStrings() {
        JsonStr obj = new JsonStr("bharat", "maheshwari");

        final var actualJson = jason.serialize(obj);

        final var expectedJson = """
                {
                \t"firstname": "bharat",
                \t"lastname": "maheshwari"
                }
                """;

        assertEquals(expectedJson, actualJson);
    }

    @Test
    @DisplayName("Testing strings, numbers and booleans")
    void testingStrNumsBooleans() {
        JsonStrNumBool obj = new JsonStrNumBool("bharat maheshwari", 19, 100.29, false);

        final var actualJson = jason.serialize(obj);

        final var expectedJson = """
                {
                \t"name": "bharat maheshwari",
                \t"age": 19,
                \t"price": 100.29,
                \t"isValid": false
                }
                """;

        assertEquals(expectedJson, actualJson);
    }

    @Test
    @DisplayName("Testing primitive arrays")
    void testingArrays() {
        JsonArray obj = new JsonArray(
                List.of(1, 1.45, "Name")
        );

        final var actualJson = jason.serialize(obj);

        final var expectedJson = """
                {
                \t"arr": [
                \t\t1,
                \t\t1.45,
                \t\t"Name"
                \t]
                }
                """;

        assertEquals(expectedJson, actualJson);
    }

    @Test
    @DisplayName("Testing object arrays")
    void testingObjectArrays() {
        JsonArray obj = new JsonArray(
                List.of(1, 1.45, "Name")
        );

        final var actualJson = jason.serialize(obj);

        final var expectedJson = """
                {
                \t"arr": [
                \t\t1,
                \t\t1.45,
                \t\t"Name"
                \t]
                }
                """;

        assertEquals(expectedJson, actualJson);
    }

    @Test
    @DisplayName("Testing nested object")
    void testingNestedObjects() {
        JsonStr strobj = new JsonStr("bharat", "maheshwari");

        JsonObj obj = new JsonObj("name", strobj);

        var actualJson = jason.serialize(obj);

        var expectedJson = """
                {
                \t"name": "name",
                \t"nested": {
                \t\t"firstname": "bharat",
                \t\t"lastname": "maheshwari"
                \t}
                }
                """;

        assertEquals(expectedJson, actualJson);

        // nested arrays
        JsonArray arr = new JsonArray(List.of("name", strobj, 1));

        actualJson = jason.serialize(arr);

        expectedJson = """
                {
                \t"arr": [
                \t\t"name",
                \t\t{
                \t\t\t"firstname": "bharat",
                \t\t\t"lastname": "maheshwari"
                \t\t},
                \t\t1
                \t]
                }
                """;

        assertEquals(expectedJson, actualJson);
    }
}