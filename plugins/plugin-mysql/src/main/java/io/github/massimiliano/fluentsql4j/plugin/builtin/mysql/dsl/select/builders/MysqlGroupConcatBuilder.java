package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.builders;

import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent builder for MySQL GROUP_CONCAT function in SELECT projections.
 * <p>
 * GROUP_CONCAT concatenates values from multiple rows into a single string.
 * This builder provides a fluent API for configuring the separator.
 * <p>
 * Example usage:
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
 */
public class MysqlGroupConcatBuilder {

    private final MysqlSelectProjectionBuilder parent;
    private final ColumnReference column;
    private String separator = ",";

    public MysqlGroupConcatBuilder(MysqlSelectProjectionBuilder parent, String table, String column) {
        this.parent = parent;
        this.column = ColumnReference.of(table, column);
    }

    /**
     * Sets the separator to use between concatenated values.
     *
     * @param separator the separator string (e.g., ", ", " | ", etc.)
     * @return this builder for method chaining
     */
    public MysqlGroupConcatBuilder separator(String separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Completes the GROUP_CONCAT configuration with an alias and returns to the parent builder.
     *
     * @param alias the alias for this projection
     * @return the parent MysqlSelectProjectionBuilder
     */
    public MysqlSelectProjectionBuilder as(String alias) {
        Map<String, Object> options = new HashMap<>();
        options.put("SEPARATOR", separator);

        CustomFunctionCall functionCall = new CustomFunctionCall("GROUP_CONCAT", List.of(column), options);

        return parent.expression(functionCall, alias);
    }
}
