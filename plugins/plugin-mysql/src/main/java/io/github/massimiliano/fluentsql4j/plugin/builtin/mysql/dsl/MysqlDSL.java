package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.PredicateExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Predicate;
import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.dsl.DSL;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;
import java.util.List;
import java.util.Map;

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
 *
 * @see DSL
 * @see io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.MysqlDialectPlugin
 * @since 1.0
 */
public class MysqlDSL extends DSL {

    /**
     * Creates a MySQL-specific DSL instance.
     *
     * @param specFactory the PreparedStatementSpecFactory used to create prepared statement specs
     * @throws NullPointerException if {@code specFactory} is {@code null}
     */
    public MysqlDSL(PreparedStatementSpecFactory specFactory) {
        super(specFactory);
    }

    /**
     * Creates a new SELECT builder with MySQL-specific projection capabilities.
     * <p>
     * This method overrides the base {@link DSL#select()} to return a MySQL-specific
     * builder that includes custom MySQL functions in the fluent API chain.
     * <p>
     * <b>Usage Example:</b>
     * <pre>{@code
     * MysqlDSL dsl = (MysqlDSL) registry.dslFor("mysql", "8.0.35").orElseThrow();
     *
     * String sql = dsl.select()
     *     .column("category")
     *     .groupConcat("name")
     *         .separator(", ")
     *         .as("names")
     *     .countStar().as("total")
     *     .from("users")
     *     .build();
     * }</pre>
     *
     * @return a MySQL-specific SELECT projection builder for fluent query construction
     */
    @Override
    public MysqlSelectProjectionBuilder select() {
        return new MysqlSelectProjectionBuilder(specFactory, this);
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
    public CustomFunctionCall groupConcat(String column, String separator) {
        java.util.Objects.requireNonNull(column, "Column must not be null");
        java.util.Objects.requireNonNull(separator, "Separator must not be null");

        return new CustomFunctionCall(
                "GROUP_CONCAT", List.of(toScalarExpression(column)), Map.of("SEPARATOR", separator));
    }

    /**
     * Internal helper method to convert various types to ScalarExpression.
     * Used by custom function builders to handle different value types.
     *
     * @param value the value to convert
     * @return the corresponding ScalarExpression
     */
    private ScalarExpression toScalarExpression(Object value) {
        return switch (value) {
            case ScalarExpression se -> se;
            case Predicate predicate -> /* Wrap predicate as an expression for use in functions like IF() */
                new PredicateExpression(predicate);
            case String str -> ColumnReference.of("", str);
            case Number n -> Literal.of(n);
            case Boolean b -> Literal.of(b);
            case null, default -> /* Fallback: treat as string literal */ Literal.of(String.valueOf(value));
        };
    }
}
