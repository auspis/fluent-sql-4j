package lan.tlab.r4j.sql.dsl.mysql;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.DSL;

/**
 * MySQL-specific DSL extension providing custom functions and features.
 * <p>
 * This class extends the base {@link DSL} to add MySQL-specific functionality,
 * including custom SQL functions like {@code GROUP_CONCAT}, {@code IF},
 * {@code DATE_FORMAT}, and others that are not part of standard SQL.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * DSLRegistry registry = DSLRegistry.createWithServiceLoader();
 * MySQLDSL dsl = (MySQLDSL) registry.dslFor("mysql", "8.0.35").orElseThrow();
 *
 * // Use MySQL-specific functions
 * String sql = dsl.select(
 *     dsl.groupConcat("name", ", ").as("names"),
 *     dsl.ifExpr(dsl.gt("age", 18), "'adult'", "'minor'").as("age_group")
 * ).from("users").build();
 * }</pre>
 * <p>
 * <b>Supported MySQL Custom Functions:</b>
 * <ul>
 *   <li>{@link #groupConcat(String, String)} - GROUP_CONCAT aggregation</li>
 *   <li>{@link #ifExpr(Object, Object, Object)} - IF conditional expression</li>
 *   <li>{@link #dateFormat(String, String)} - DATE_FORMAT for date formatting</li>
 *   <li>More to be added as needed</li>
 * </ul>
 *
 * @see DSL
 * @see lan.tlab.r4j.sql.plugin.builtin.mysql.MysqlDialectPlugin
 * @since 1.0
 */
public class MysqlDSL extends DSL {

    /**
     * Creates a MySQL-specific DSL instance.
     *
     * @param renderer the MySQL dialect renderer
     * @throws NullPointerException if {@code renderer} is {@code null}
     */
    public MysqlDSL(DialectRenderer renderer) {
        super(renderer);
    }

    /**
     * Creates a MySQL GROUP_CONCAT aggregate function.
     * <p>
     * GROUP_CONCAT concatenates values from multiple rows into a single string,
     * with an optional separator between values.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * dsl.select(
     *     "category",
     *     dsl.groupConcat("product_name", ", ").as("products")
     * )
     * .from("products")
     * .groupBy("category")
     * .build();
     * // Result: SELECT category, GROUP_CONCAT(product_name SEPARATOR ', ') AS products
     * //         FROM products GROUP BY category
     * }</pre>
     *
     * @param column the column to concatenate
     * @param separator the separator between values (e.g., ", ", "|", etc.)
     * @return a custom function call representing GROUP_CONCAT
     * @throws NullPointerException if {@code column} or {@code separator} is {@code null}
     */
    public Object groupConcat(String column, String separator) {
        java.util.Objects.requireNonNull(column, "Column must not be null");
        java.util.Objects.requireNonNull(separator, "Separator must not be null");
        // TODO: Implement using CustomFunctionCall
        throw new UnsupportedOperationException("groupConcat() will be implemented in Task 8");
    }

    /**
     * Creates a MySQL IF conditional expression.
     * <p>
     * The IF function returns one value if a condition is true, and another value if false.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * dsl.select(
     *     "name",
     *     dsl.ifExpr(dsl.gt("age", 18), "'adult'", "'minor'").as("age_group")
     * )
     * .from("users")
     * .build();
     * // Result: SELECT name, IF(age > 18, 'adult', 'minor') AS age_group FROM users
     * }</pre>
     *
     * @param condition the condition to evaluate
     * @param trueValue the value to return if condition is true
     * @param falseValue the value to return if condition is false
     * @return a custom function call representing IF
     * @throws NullPointerException if any parameter is {@code null}
     */
    public Object ifExpr(Object condition, Object trueValue, Object falseValue) {
        java.util.Objects.requireNonNull(condition, "Condition must not be null");
        java.util.Objects.requireNonNull(trueValue, "True value must not be null");
        java.util.Objects.requireNonNull(falseValue, "False value must not be null");
        // TODO: Implement using CustomFunctionCall
        throw new UnsupportedOperationException("ifExpr() will be implemented in Task 8");
    }

    /**
     * Creates a MySQL DATE_FORMAT function.
     * <p>
     * DATE_FORMAT formats a date value according to a specified format string.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * dsl.select(
     *     "name",
     *     dsl.dateFormat("birth_date", "%Y-%m-%d").as("formatted_date")
     * )
     * .from("users")
     * .build();
     * // Result: SELECT name, DATE_FORMAT(birth_date, '%Y-%m-%d') AS formatted_date
     * //         FROM users
     * }</pre>
     * <p>
     * <b>Common format specifiers:</b>
     * <ul>
     *   <li>{@code %Y} - Year (4 digits)</li>
     *   <li>{@code %m} - Month (01-12)</li>
     *   <li>{@code %d} - Day (01-31)</li>
     *   <li>{@code %H} - Hour (00-23)</li>
     *   <li>{@code %i} - Minutes (00-59)</li>
     *   <li>{@code %s} - Seconds (00-59)</li>
     * </ul>
     *
     * @param dateColumn the date column to format
     * @param format the format string using MySQL date format specifiers
     * @return a custom function call representing DATE_FORMAT
     * @throws NullPointerException if any parameter is {@code null}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-format">MySQL DATE_FORMAT Documentation</a>
     */
    public Object dateFormat(String dateColumn, String format) {
        java.util.Objects.requireNonNull(dateColumn, "Date column must not be null");
        java.util.Objects.requireNonNull(format, "Format must not be null");
        // TODO: Implement using CustomFunctionCall
        throw new UnsupportedOperationException("dateFormat() will be implemented in Task 8");
    }

    /**
     * Creates a MySQL NOW() function.
     * <p>
     * Returns the current date and time as a DATETIME value.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * dsl.insertInto("logs")
     *     .values("event", "user_login")
     *     .values("timestamp", dsl.now())
     *     .build();
     * // Result: INSERT INTO logs (event, timestamp) VALUES ('user_login', NOW())
     * }</pre>
     *
     * @return a custom function call representing NOW()
     */
    public Object now() {
        // TODO: Implement using CustomFunctionCall
        throw new UnsupportedOperationException("now() will be implemented in Task 8");
    }

    /**
     * Creates a MySQL CURDATE() function.
     * <p>
     * Returns the current date as a DATE value (without time).
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * dsl.select("name", "birth_date")
     *     .from("users")
     *     .where(dsl.eq("birth_date", dsl.curDate()))
     *     .build();
     * // Result: SELECT name, birth_date FROM users WHERE birth_date = CURDATE()
     * }</pre>
     *
     * @return a custom function call representing CURDATE()
     */
    public Object curDate() {
        // TODO: Implement using CustomFunctionCall
        throw new UnsupportedOperationException("curDate() will be implemented in Task 8");
    }
}
