package io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select;

import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.select.SelectProjectionBuilder;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.MysqlDSL;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.builders.MysqlCoalesceBuilder;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.builders.MysqlConcatBuilder;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.builders.MysqlGroupConcatBuilder;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.builders.MysqlIfBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MySQL-specific SelectProjectionBuilder that adds MySQL custom functions.
 * <p>
 * This builder extends the standard SelectProjectionBuilder with MySQL-specific
 * aggregate functions like GROUP_CONCAT.
 * <p>
 * Example usage:
 * <pre>{@code
 * MysqlDSL dsl = ...;
 * dsl.select()
 *     .column("category")
 *     .groupConcat("product_name").separator(", ").as("products")
 *     .from("products")
 *     .groupBy("category")
 *     .build();
 * }</pre>
 */
public class MysqlSelectProjectionBuilder extends SelectProjectionBuilder<MysqlSelectProjectionBuilder> {

    private final MysqlDSL dsl;

    public MysqlSelectProjectionBuilder(PreparedStatementSpecFactory specFactory, MysqlDSL dsl) {
        super(specFactory);
        this.dsl = dsl;
    }

    /**
     * Starts building a MySQL GROUP_CONCAT aggregate function.
     * <p>
     * GROUP_CONCAT concatenates values from multiple rows into a single string,
     * with a configurable separator.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .column("category")
     *     .groupConcat("product_name").separator(", ").as("products")
     *     .from("products")
     *     .groupBy("category")
     *     .build();
     * // Result: SELECT category, GROUP_CONCAT(product_name SEPARATOR ', ') AS products
     * //         FROM products GROUP BY category
     * }</pre>
     *
     * @param column the column to concatenate
     * @return a MysqlGroupConcatBuilder for further configuration
     */
    public MysqlGroupConcatBuilder groupConcat(String column) {
        return groupConcat("", column);
    }

    /**
     * Starts building a MySQL GROUP_CONCAT aggregate function with explicit table reference.
     *
     * @param table the table name
     * @param column the column to concatenate
     * @return a MysqlGroupConcatBuilder for further configuration
     */
    public MysqlGroupConcatBuilder groupConcat(String table, String column) {
        finalizePendingProjection();
        return new MysqlGroupConcatBuilder(this, table, column);
    }

    /**
     * Starts building a MySQL IF conditional function.
     * <p>
     * The IF function returns one value if a condition is true, and another value otherwise.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .column("name")
     *     .column("age")
     *     .ifExpr()
     *         .when("age").gte(18)
     *         .then("adult")
     *         .otherwise("minor")
     *         .as("age_category")
     *     .from("users")
     *     .build();
     * // Result: SELECT name, age, IF(age >= 18, 'adult', 'minor') AS age_category FROM users
     * }</pre>
     *
     * @return a MysqlIfBuilder to start specifying the condition
     */
    public MysqlIfBuilder ifExpr() {
        finalizePendingProjection();
        return new MysqlIfBuilder(this, dsl);
    }

    public MysqlSelectProjectionBuilder dateFormat(String column, String format) {
        return dateFormat("", column, format);
    }

    public MysqlSelectProjectionBuilder dateFormat(String table, String column, String format) {
        Objects.requireNonNull(column, "Column must not be null");
        Objects.requireNonNull(format, "Format must not be null");

        ColumnReference columnRef =
                table != null && !table.isEmpty() ? ColumnReference.of(table, column) : ColumnReference.of("", column);

        Literal<String> formatLiteral = Literal.of(format);

        CustomFunctionCall dateFormatCall =
                new CustomFunctionCall("DATE_FORMAT", List.of(columnRef, formatLiteral), Map.of());

        return expression(dateFormatCall);
    }

    /**
     * Adds a MySQL IFNULL function with column reference (no table).
     * <p>
     * IFNULL returns the column value if not NULL, otherwise returns the default value.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .ifnull("email", "no-email@example.com").as("contact_email")
     *     .from("users")
     *     .build();
     * // Result: SELECT IFNULL(email, 'no-email@example.com') AS contact_email FROM users
     * }</pre>
     *
     * @param column the column name
     * @param defaultValue the default value if column is NULL
     * @return this builder for method chaining
     */
    public MysqlSelectProjectionBuilder ifnull(String column, String defaultValue) {
        return ifnull("", column, defaultValue);
    }

    /**
     * Adds a MySQL IFNULL function with table-qualified column reference.
     *
     * @param table the table name
     * @param column the column name
     * @param defaultValue the default value if column is NULL
     * @return this builder for method chaining
     */
    public MysqlSelectProjectionBuilder ifnull(String table, String column, String defaultValue) {
        Objects.requireNonNull(column, "Column must not be null");
        Objects.requireNonNull(defaultValue, "Default value must not be null");

        ColumnReference columnRef =
                table != null && !table.isEmpty() ? ColumnReference.of(table, column) : ColumnReference.of("", column);

        Literal<String> defaultLiteral = Literal.of(defaultValue);

        CustomFunctionCall ifnullCall = new CustomFunctionCall("IFNULL", List.of(columnRef, defaultLiteral), Map.of());

        return expression(ifnullCall);
    }

    /**
     * Adds a MySQL IFNULL function with scalar expressions.
     * <p>
     * This version allows using any scalar expression for both the value and default.
     *
     * @param expression the expression to check for NULL
     * @param defaultExpression the expression to return if first is NULL
     * @return this builder for method chaining
     */
    public MysqlSelectProjectionBuilder ifnull(ScalarExpression expression, ScalarExpression defaultExpression) {
        Objects.requireNonNull(expression, "Expression must not be null");
        Objects.requireNonNull(defaultExpression, "Default expression must not be null");

        CustomFunctionCall ifnullCall =
                new CustomFunctionCall("IFNULL", List.of(expression, defaultExpression), Map.of());

        return expression(ifnullCall);
    }

    /**
     * Starts building a MySQL CONCAT function.
     * <p>
     * CONCAT combines multiple string values into a single string.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .concat()
     *         .column("first_name")
     *         .literal(" ")
     *         .column("users", "last_name")
     *         .as("full_name")
     *     .from("users")
     *     .build();
     * // Result: SELECT CONCAT(first_name, ' ', users.last_name) AS full_name FROM users
     * }</pre>
     *
     * @return a MysqlConcatBuilder for building the CONCAT expression
     */
    public MysqlConcatBuilder concat() {
        finalizePendingProjection();
        return new MysqlConcatBuilder(this);
    }

    /**
     * Starts building a MySQL COALESCE function.
     * <p>
     * COALESCE returns the first non-NULL expression from the list.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .coalesce()
     *         .column("mobile_phone")
     *         .column("home_phone")
     *         .literal("no-phone")
     *         .as("contact_number")
     *     .from("users")
     *     .build();
     * // Result: SELECT COALESCE(mobile_phone, home_phone, 'no-phone') AS contact_number FROM users
     * }</pre>
     *
     * @return a MysqlCoalesceBuilder for building the COALESCE expression
     */
    public MysqlCoalesceBuilder coalesce() {
        finalizePendingProjection();
        return new MysqlCoalesceBuilder(this);
    }
}
