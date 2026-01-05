package io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.builders;

import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builder for MySQL CONCAT function.
 * <p>
 * CONCAT combines multiple string values into a single string.
 * <p>
 * Example usage:
 * <pre>{@code
 * dsl.select()
 *     .concat()
 *         .add("first_name")
 *         .add(" ")  // literal space
 *         .add("users", "last_name")
 *         .as("full_name")
 *     .from("users")
 *     .build();
 * // Result: SELECT CONCAT(first_name, ' ', users.last_name) AS full_name FROM users
 * }</pre>
 */
public class MysqlConcatBuilder {

    private final MysqlSelectProjectionBuilder parent;
    private final List<ScalarExpression> expressions;

    public MysqlConcatBuilder(MysqlSelectProjectionBuilder parent) {
        this.parent = parent;
        this.expressions = new ArrayList<>();
    }

    /**
     * Adds a column reference without table qualification.
     *
     * @param column the column name
     * @return this builder for method chaining
     */
    public MysqlConcatBuilder column(String column) {
        Objects.requireNonNull(column, "Column must not be null");
        expressions.add(ColumnReference.of("", column));
        return this;
    }

    public MysqlConcatBuilder column(String table, String column) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(column, "Column must not be null");
        expressions.add(ColumnReference.of(table, column));
        return this;
    }

    public MysqlConcatBuilder expression(ScalarExpression expression) {
        Objects.requireNonNull(expression, "Expression must not be null");
        expressions.add(expression);
        return this;
    }

    public MysqlConcatBuilder literal(Object value) {
        Objects.requireNonNull(value, "Value must not be null");
        expressions.add(Literal.of(String.valueOf(value)));
        return this;
    }

    /**
     * Finalizes the CONCAT function with an alias and returns to the projection builder.
     *
     * @param alias the alias for the CONCAT result
     * @return the parent MysqlSelectProjectionBuilder for continued query building
     */
    public MysqlSelectProjectionBuilder as(String alias) {
        if (expressions.isEmpty()) {
            throw new IllegalStateException("CONCAT requires at least one expression");
        }

        CustomFunctionCall concatCall = new CustomFunctionCall("CONCAT", expressions, Map.of());
        return parent.expression(concatCall).as(alias);
    }
}
