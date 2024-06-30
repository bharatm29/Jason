package org.bharat;

import org.bharat.jsonObjs.JsonStr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

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
}