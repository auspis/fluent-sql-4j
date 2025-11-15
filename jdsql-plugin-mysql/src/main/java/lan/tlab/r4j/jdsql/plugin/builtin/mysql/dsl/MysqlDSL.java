package lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl;

import java.util.List;
import java.util.Map;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;

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
 * @see lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlDialectPlugin
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
        return new MysqlSelectProjectionBuilder(renderer, this);
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
        if (value instanceof ScalarExpression se) {
            return se;
        }
        if (value instanceof lan.tlab.r4j.jdsql.ast.common.predicate.Predicate predicate) {
            // Wrap predicate as an expression for use in functions like IF()
            return new lan.tlab.r4j.jdsql.ast.common.expression.scalar.PredicateExpression(predicate);
        }
        if (value instanceof String str) {
            return ColumnReference.of("", str);
        }
        if (value instanceof Number n) {
            return Literal.of(n);
        }
        if (value instanceof Boolean b) {
            return Literal.of(b);
        }
        // Fallback: treat as string literal
        return Literal.of(String.valueOf(value));
    }
}
