package org.bharat;

import org.bharat.jsonObjs.JsonDeezArray;
import org.bharat.jsonObjs.JsonDeezObjArray;
import org.bharat.jsonObjs.JsonStr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("While testing JsonDeserialize")
class JasonDeserializeTest {
    @Test
    @DisplayName("Testing simple deserialization")
    void testingSimpleDeserialization() {
        final String json = """
                {
                \t"firstname": "Bharat",
                \t"lastname": "Maheshwari"
                }
                """;

        JasonDeserialize<JsonStr> deserialize = new JasonDeserialize<>();

        final JsonStr actual = deserialize.deserialize(json, JsonStr.class);

        final JsonStr expected = new JsonStr("Bharat", "Maheshwari");

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Testing array deserialization")
    void testingArrayDeserialization() {
        final String json = """
                {
                \t"arr": [
                \t\t"name1",
                \t\t"name2"
                \t]
                }
                """;

        JasonDeserialize<JsonDeezArray> deserialize = new JasonDeserialize<>();

        final JsonDeezArray actual = deserialize.deserialize(json, JsonDeezArray.class);

        final JsonDeezArray expected = new JsonDeezArray(List.of("name1", "name2"));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Testing object array deserialization")
    void testingObjectArrayDeserialization() {
        final String json = """
                {
                \t"arr": [
                \t\t{
                \t\t\t"firstname": "name1",
                \t\t\t"lastname": "name2"
                \t\t},
                \t\t{
                \t\t\t"firstname": "name3",
                \t\t\t"lastname": "name4"
                \t\t}
                \t]
                }
                """;

        JasonDeserialize<JsonDeezObjArray> deserialize = new JasonDeserialize<>();

        final JsonDeezObjArray actual = deserialize.deserialize(json, JsonDeezObjArray.class);

        final JsonDeezObjArray expected = new JsonDeezObjArray(List.of(
                new JsonStr("name1", "name2"),
                new JsonStr("name3", "name4")
        ));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Testing if it ignores when field with that name doesn't exist")
    void testingThrowsIfFieldNonExist() {
        final String json = """
                {
                \t"firstname": "Bharat",
                \t"lastname": "Maheshwari"
                \t"non-existent-field": "non-existent-value"
                }
                """;

        JasonDeserialize<JsonStr> deserialize = new JasonDeserialize<>();

        final JsonStr actual = deserialize.deserialize(json, JsonStr.class);

        final JsonStr expected = new JsonStr("Bharat", "Maheshwari");

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Testing if throws error when there isn't enough fields to create the object in case of records")
    void testingThrowsIfNotEnoughFields() {
        final String json = """
                {
                \t"lastname": "Maheshwari"
                }
                """;

        assertThrows(RuntimeException.class, () -> {
            JasonDeserialize<JsonStr> deserialize = new JasonDeserialize<>();

            deserialize.deserialize(json, JsonStr.class);
        });
    }


    @Test
    @DisplayName("Testing simple deserialization with null values")
    void testingSimpleNullDeserialization() {
        final String json = """
                {
                \t"firstname": null,
                \t"lastname": "Maheshwari"
                }
                """;

        JasonDeserialize<JsonStr> deserialize = new JasonDeserialize<>();

        final JsonStr actual = deserialize.deserialize(json, JsonStr.class);

        final JsonStr expected = new JsonStr(null, "Maheshwari");

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}