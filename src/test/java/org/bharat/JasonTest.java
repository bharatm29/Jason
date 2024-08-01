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
                  "firstname": "bharat",
                  "lastname": "maheshwari"
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
                  "name": "bharat maheshwari",
                  "age": 19,
                  "price": 100.29,
                  "isValid": false
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
                  "arr": [
                    1,
                    1.45,
                    "Name"
                  ]
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
                  "arr": [
                    1,
                    1.45,
                    "Name"
                  ]
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
                  "name": "name",
                  "nested": {
                    "firstname": "bharat",
                    "lastname": "maheshwari"
                  }
                }
                """;

        assertEquals(expectedJson, actualJson);

        // nested arrays
        JsonArray arr = new JsonArray(List.of("name", strobj, 1));

        actualJson = jason.serialize(arr);

        expectedJson = """
                {
                  "arr": [
                    "name",
                    {
                      "firstname": "bharat",
                      "lastname": "maheshwari"
                    },
                    1
                  ]
                }
                """;

        assertEquals(expectedJson, actualJson);
    }

    @Test
    @DisplayName("Testing simple strings with null values")
    void testingSimpleStringsWithNull() {
        JsonStr obj = new JsonStr("bharat", null);

        final var actualJson = jason.serialize(obj);

        final var expectedJson = """
                {
                  "firstname": "bharat",
                  "lastname": null
                }
                """;

        assertEquals(expectedJson, actualJson);
    }


    @Test
    @DisplayName("Testing nested object with null values")
    void testingNestedObjectsWithNull() {
        JsonStr strobj = new JsonStr(null, "maheshwari");

        JsonObj obj = new JsonObj("name", strobj);

        var actualJson = jason.serialize(obj);

        var expectedJson = """
                {
                  "name": "name",
                  "nested": {
                    "firstname": null,
                    "lastname": "maheshwari"
                  }
                }
                """;

        assertEquals(expectedJson, actualJson);
    }
}