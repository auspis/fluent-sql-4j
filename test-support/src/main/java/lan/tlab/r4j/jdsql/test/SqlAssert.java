package lan.tlab.r4j.jdsql.test;

import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.assertj.core.api.AbstractAssert;

/**
 * AssertJ custom assertion for SQL strings that provides fluent assertions.
 * <p>
 * This assertion provides convenient methods for asserting on SQL statements,
 * including:
 * - Exact equality comparison
 * - Substring containment (single or multiple fragments)
 * - Fragment ordering verification
 * - Whitespace normalization for comparison
 * <p>
 * This is a helper class that provides custom assertions for SQL strings generated
 * by the DSL builders and renderers.
 *
 * @see <a href="https://www.baeldung.com/java-helper-vs-utility-classes">Helper vs Utility Classes</a>
 */
public class SqlAssert extends AbstractAssert<SqlAssert, String> {

    private SqlAssert(String actual) {
        super(actual, SqlAssert.class);
    }

    /**
     * Create a new SqlAssert instance for the given SqlCaptureHelper.
     *
     * @param actual the actual SqlCaptureHelper to assert
     * @return a new SqlAssert instance
     */
    public static SqlAssert assertThatSql(SqlCaptureHelper actual) {
        return assertThatSql(actual.getSql());
    }

    /**
     * Create a new SqlAssert instance for the given SQL string.
     *
     * @param actual the actual SQL string to assert
     * @return a new SqlAssert instance
     */
    private static SqlAssert assertThatSql(String actual) {
        return new SqlAssert(actual);
    }

    /**
     * Verifies that the actual SQL string is exactly equal to the expected SQL string.
     * <p>
     * This performs a strict string equality check.
     *
     * @param expected the expected SQL string
     * @return this assertion object for method chaining
     * @throws AssertionError if the SQL strings are not equal
     */
    public SqlAssert isEqualTo(String expected) {
        isNotNull();

        if (!actual.equals(expected)) {
            failWithMessage("""
                        Expected SQL to be equal but was not.%n\
                        Expected: %s%n\
                        Actual:   %s""", expected, actual);
        }

        return this;
    }

    /**
     * Verifies that the actual SQL string is equal to the expected SQL string after normalizing whitespace.
     * <p>
     * This comparison:
     * - Trims leading/trailing whitespace
     * - Replaces multiple spaces with single space
     * - Normalizes line breaks
     * <p>
     * Useful for comparing multi-line SQL or SQL with inconsistent formatting.
     *
     * @param expected the expected SQL string
     * @return this assertion object for method chaining
     * @throws AssertionError if the SQL strings are not equal after normalization
     */
    public SqlAssert isEqualToNormalizingWhitespace(String expected) {
        isNotNull();

        String normalizedActual = normalizeWhitespace(actual);
        String normalizedExpected = normalizeWhitespace(expected);

        if (!normalizedActual.equals(normalizedExpected)) {
            failWithMessage("""
                        Expected SQL to be equal (after normalizing whitespace) but was not.%n\
                        Expected: %s%n\
                        Actual:   %s""", normalizedExpected, normalizedActual);
        }

        return this;
    }

    /**
     * Verifies that the actual SQL string contains the specified fragment.
     *
     * @param fragment the SQL fragment expected to be present
     * @return this assertion object for method chaining
     * @throws AssertionError if the fragment is not found
     */
    public SqlAssert contains(String fragment) {
        isNotNull();

        if (!actual.contains(fragment)) {
            failWithMessage(
                    "Expected SQL to contain '%s' but it was not found.%n" + "Actual SQL: %s", fragment, actual);
        }

        return this;
    }

    /**
     * Verifies that the actual SQL string contains all the specified fragments.
     * <p>
     * The order of fragments is not verified by this method.
     *
     * @param fragments the SQL fragments expected to be present
     * @return this assertion object for method chaining
     * @throws AssertionError if any fragment is not found
     */
    public SqlAssert containsAll(String... fragments) {
        isNotNull();

        for (String fragment : fragments) {
            if (!actual.contains(fragment)) {
                failWithMessage(
                        "Expected SQL to contain '%s' but it was not found.%n" + "Actual SQL: %s", fragment, actual);
            }
        }

        return this;
    }

    /**
     * Verifies that the actual SQL string contains all the specified fragments in the given order.
     * <p>
     * This method checks that each fragment appears after the previous one.
     *
     * @param fragments the SQL fragments expected to be present in order
     * @return this assertion object for method chaining
     * @throws AssertionError if any fragment is not found or fragments are out of order
     */
    public SqlAssert containsInOrder(String... fragments) {
        isNotNull();

        int lastIndex = -1;
        for (String fragment : fragments) {
            int currentIndex = actual.indexOf(fragment, lastIndex + 1);
            if (currentIndex == -1) {
                failWithMessage(
                        "Expected SQL to contain '%s' after previous fragments but it was not found.%n"
                                + "Actual SQL: %s",
                        fragment, actual);
            }
            lastIndex = currentIndex;
        }

        return this;
    }

    /**
     * Verifies that the actual SQL string does not contain the specified fragment.
     *
     * @param fragment the SQL fragment expected to be absent
     * @return this assertion object for method chaining
     * @throws AssertionError if the fragment is found
     */
    public SqlAssert doesNotContain(String fragment) {
        isNotNull();

        if (actual.contains(fragment)) {
            failWithMessage(
                    "Expected SQL to not contain '%s' but it was found.%n" + "Actual SQL: %s", fragment, actual);
        }

        return this;
    }

    /**
     * Verifies that the actual SQL string starts with the specified prefix.
     *
     * @param prefix the expected SQL prefix
     * @return this assertion object for method chaining
     * @throws AssertionError if the SQL doesn't start with the prefix
     */
    public SqlAssert startsWith(String prefix) {
        isNotNull();

        if (!actual.startsWith(prefix)) {
            failWithMessage("Expected SQL to start with '%s' but it did not.%n" + "Actual SQL: %s", prefix, actual);
        }

        return this;
    }

    /**
     * Verifies that the actual SQL string ends with the specified suffix.
     *
     * @param suffix the expected SQL suffix
     * @return this assertion object for method chaining
     * @throws AssertionError if the SQL doesn't end with the suffix
     */
    public SqlAssert endsWith(String suffix) {
        isNotNull();

        if (!actual.endsWith(suffix)) {
            failWithMessage("Expected SQL to end with '%s' but it did not.%n" + "Actual SQL: %s", suffix, actual);
        }

        return this;
    }

    /**
     * Normalize whitespace in SQL string for comparison.
     * <p>
     * This method:
     * - Trims leading/trailing whitespace
     * - Replaces all sequences of whitespace characters (spaces, tabs, newlines) with a single space
     */
    private String normalizeWhitespace(String sql) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }
        // Replace all sequences of whitespace with single space and trim
        return sql.trim().replaceAll("\\s+", " ");
    }
}
