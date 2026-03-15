package io.github.auspis.fluentsql4j.test;

import static io.github.auspis.fluentsql4j.test.JsonAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class JsonAssertTest {

    // --- isEqualToJson ---

    @Test
    void isEqualToJsonPassesForIdenticalJson() {
        assertThatJson("{\"name\":\"Alice\",\"age\":30}").isEqualToJson("{\"name\":\"Alice\",\"age\":30}");
    }

    @Test
    void isEqualToJsonIgnoresPropertyOrdering() {
        assertThatJson("{\"age\":30,\"name\":\"Alice\"}").isEqualToJson("{\"name\":\"Alice\",\"age\":30}");
    }

    @Test
    void isEqualToJsonIgnoresWhitespaceDifferences() {
        assertThatJson("{  \"key\" :  \"value\"  }").isEqualToJson("{\"key\":\"value\"}");
    }

    @Test
    void isEqualToJsonFailsWhenJsonDiffers() {
        JsonAssert assertion = assertThatJson("{\"a\":1}");
        assertThatThrownBy(() -> assertion.isEqualToJson("{\"a\":2}")).isInstanceOf(AssertionError.class);
    }

    @Test
    void isEqualToJsonHandlesH2EscapedJsonFormat() {
        // H2 returns JSON strings wrapped in outer quotes with inner quotes escaped
        String h2Style = "\"{\\\"key\\\":\\\"value\\\"}\"";
        assertThatJson(h2Style).isEqualToJson("{\"key\":\"value\"}");
    }

    @Test
    void isEqualToJsonFailsForInvalidJson() {
        JsonAssert assertion = assertThatJson("not-json");
        assertThatThrownBy(() -> assertion.isEqualToJson("{\"a\":1}")).isInstanceOf(AssertionError.class);
    }

    @Test
    void isEqualToJsonSupportsArrays() {
        assertThatJson("[1,2,3]").isEqualToJson("[1,2,3]");
    }

    // --- containsAllJsonArrayValues ---

    @Test
    void containsAllJsonArrayValuesPassesWhenAllFound() {
        assertThatJson("[\"apple\",\"banana\",\"cherry\"]").containsAllJsonArrayValues("apple", "cherry");
    }

    @Test
    void containsAllJsonArrayValuesFailsWhenElementMissing() {
        JsonAssert assertion = assertThatJson("[\"apple\",\"banana\"]");
        assertThatThrownBy(() -> assertion.containsAllJsonArrayValues("grape")).isInstanceOf(AssertionError.class);
    }

    @Test
    void containsAllJsonArrayValuesFailsWhenNotAnArray() {
        JsonAssert assertion = assertThatJson("{\"key\":\"value\"}");
        assertThatThrownBy(() -> assertion.containsAllJsonArrayValues("value")).isInstanceOf(AssertionError.class);
    }

    // --- hasJsonProperty ---

    @Test
    void hasJsonPropertyPassesForMatchingTextualProperty() {
        assertThatJson("{\"name\":\"Alice\"}").hasJsonProperty("name", "Alice");
    }

    @Test
    void hasJsonPropertyPassesForNonTextualPropertyUsingToString() {
        // numeric property: not textual, uses toString() -> "42"
        assertThatJson("{\"count\":42}").hasJsonProperty("count", "42");
    }

    @Test
    void hasJsonPropertyFailsWhenPropertyValueDoesNotMatch() {
        JsonAssert assertion = assertThatJson("{\"name\":\"Alice\"}");
        assertThatThrownBy(() -> assertion.hasJsonProperty("name", "Bob")).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasJsonPropertyFailsWhenPropertyMissing() {
        JsonAssert assertion = assertThatJson("{\"name\":\"Alice\"}");
        assertThatThrownBy(() -> assertion.hasJsonProperty("age", "30")).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasJsonPropertyFailsWhenNotAnObject() {
        JsonAssert assertion = assertThatJson("[\"a\",\"b\"]");
        assertThatThrownBy(() -> assertion.hasJsonProperty("name", "Alice")).isInstanceOf(AssertionError.class);
    }

    // --- normalizeEscapedJson edge cases (exercised via isEqualToJson) ---

    @Test
    void normalizeHandlesPlainJsonWithoutEscaping() {
        // Plain JSON does NOT start with " -- normalization is a no-op
        assertThatJson("{\"key\":\"value\"}").isEqualToJson("{\"key\":\"value\"}");
    }

    @Test
    void normalizeHandlesStringStartingWithQuoteButNoEscapedQuotesInside() {
        // Starts and ends with " but no \" inside — treated as plain JSON string value
        assertThatJson("\"plainstring\"").isEqualToJson("\"plainstring\"");
    }
}
