package lan.tlab.r4j.integration.sql.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import org.assertj.core.api.AbstractAssert;

/**
 * AssertJ custom assertion for JSON strings that performs semantic comparison.
 * <p>
 * This assertion parses JSON strings and compares them semantically, ignoring:
 * - Whitespace differences
 * - Property order
 * - String escape format differences between JDBC drivers
 * <p>
 * This is a helper class that provides custom assertions for JSON values retrieved
 * from different database drivers, which may return JSON in different string formats.
 *
 * @see <a href="https://www.baeldung.com/java-helper-vs-utility-classes">Helper vs Utility Classes</a>
 */
public class JsonAssert extends AbstractAssert<JsonAssert, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonAssert(String actual) {
        super(actual, JsonAssert.class);
    }

    /**
     * Create a new JsonAssert instance for the given JSON string.
     *
     * @param actual the actual JSON string to assert
     * @return a new JsonAssert instance
     */
    public static JsonAssert assertThatJson(String actual) {
        return new JsonAssert(actual);
    }

    /**
     * Verifies that the actual JSON string is semantically equal to the expected JSON string.
     * <p>
     * This method parses both JSON strings and compares them as JsonNode objects,
     * which ignores whitespace, property order, and escape format differences.
     *
     * @param expected the expected JSON string
     * @return this assertion object for method chaining
     * @throws AssertionError if the JSON strings are not semantically equal
     */
    public JsonAssert isEqualToJson(String expected) {
        isNotNull();

        JsonNode actualNode = parseJson(actual, "actual");
        JsonNode expectedNode = parseJson(expected, "expected");

        if (!actualNode.equals(expectedNode)) {
            failWithMessage(
                    "Expected JSON to be equal but was not.%n" + "Expected: %s%n" + "Actual:   %s",
                    expectedNode.toString(), actualNode.toString());
        }

        return this;
    }

    /**
     * Verifies that the actual JSON array contains all the expected values.
     * <p>
     * This method is useful for asserting on JSON arrays where order might vary.
     *
     * @param expectedValues the values expected to be in the JSON array
     * @return this assertion object for method chaining
     * @throws AssertionError if any expected value is not found in the array
     */
    public JsonAssert containsAllJsonArrayValues(String... expectedValues) {
        isNotNull();

        JsonNode actualNode = parseJson(actual, "actual");

        if (!actualNode.isArray()) {
            failWithMessage("Expected JSON array but got: %s", actualNode.getNodeType());
        }

        for (String expectedValue : expectedValues) {
            boolean found = false;
            for (JsonNode element : actualNode) {
                if (element.isTextual() && element.asText().equals(expectedValue)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                failWithMessage(
                        "Expected JSON array to contain '%s' but it was not found.%n" + "Actual array: %s",
                        expectedValue, actualNode.toString());
            }
        }

        return this;
    }

    /**
     * Verifies that the actual JSON object has the expected property with the expected value.
     *
     * @param propertyName the name of the property to check
     * @param expectedValue the expected value of the property
     * @return this assertion object for method chaining
     * @throws AssertionError if the property is missing or has a different value
     */
    public JsonAssert hasJsonProperty(String propertyName, String expectedValue) {
        isNotNull();

        JsonNode actualNode = parseJson(actual, "actual");

        if (!actualNode.isObject()) {
            failWithMessage("Expected JSON object but got: %s", actualNode.getNodeType());
        }

        if (!actualNode.has(propertyName)) {
            failWithMessage(
                    "Expected JSON object to have property '%s' but it was not found.%n" + "Actual object: %s",
                    propertyName, actualNode.toString());
        }

        JsonNode propertyNode = actualNode.get(propertyName);
        String actualValue = propertyNode.isTextual() ? propertyNode.asText() : propertyNode.toString();

        if (!actualValue.equals(expectedValue)) {
            failWithMessage(
                    "Expected property '%s' to have value '%s' but was '%s'", propertyName, expectedValue, actualValue);
        }

        return this;
    }

    private JsonNode parseJson(String json, String context) {
        try {
            // Handle H2's escaped JSON format: H2 returns JSON as a string with escaped quotes
            // e.g., "{\"key\":\"value\"}" instead of {"key":"value"}
            // We need to unescape it before parsing
            String normalizedJson = normalizeEscapedJson(json);
            return OBJECT_MAPPER.readTree(normalizedJson);
        } catch (IOException e) {
            failWithMessage("Failed to parse %s JSON: %s%nJSON: %s", context, e.getMessage(), json);
            return null; // Never reached due to failWithMessage
        }
    }

    /**
     * Normalize escaped JSON strings returned by some JDBC drivers (like H2).
     * <p>
     * Some databases return JSON columns as escaped strings:
     * - H2: "{\"key\":\"value\"}" (with quotes around and escaped inner quotes)
     * - MySQL: {"key":"value"} (plain JSON)
     * <p>
     * This method detects and un-escapes H2-style JSON strings.
     */
    private String normalizeEscapedJson(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }

        // Check if it's an escaped JSON string (starts and ends with quotes, has escaped quotes inside)
        if (json.startsWith("\"") && json.endsWith("\"") && json.contains("\\\"")) {
            // Remove outer quotes and unescape inner quotes
            String unescaped = json.substring(1, json.length() - 1);
            unescaped = unescaped.replace("\\\"", "\"");
            return unescaped;
        }

        return json;
    }
}
