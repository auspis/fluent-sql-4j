package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.builders;

import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builder for MySQL COALESCE function.
 * <p>
 * COALESCE returns the first non-NULL expression from the list.
 * <p>
 * Example usage:
 * <pre>{@code
 * dsl.select()
 *     .coalesce()
 *         .add("mobile_phone")
 *         .add("home_phone")
 *         .add("users", "work_phone")
 *         .addLiteral("no-phone")
 *         .as("contact_number")
 *     .from("users")
 *     .build();
 * // Result: SELECT COALESCE(mobile_phone, home_phone, users.work_phone, 'no-phone') AS contact_number FROM users
 * }</pre>
 */
public class MysqlCoalesceBuilder {

    private final MysqlSelectProjectionBuilder parent;
    private final List<ScalarExpression> expressions;

    public MysqlCoalesceBuilder(MysqlSelectProjectionBuilder parent) {
        this.parent = parent;
        this.expressions = new ArrayList<>();
    }

    /**
     * Adds a column reference without table qualification.
     *
     * @param column the column name
     * @return this builder for method chaining
     */
    public MysqlCoalesceBuilder column(String column) {
        Objects.requireNonNull(column, "Column must not be null");
        expressions.add(ColumnReference.of("", column));
        return this;
    }

    /**
     * Adds a column reference with table qualification.
     *
     * @param table the table name
     * @param column the column name
     * @return this builder for method chaining
     */
    public MysqlCoalesceBuilder column(String table, String column) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(column, "Column must not be null");
        expressions.add(ColumnReference.of(table, column));
        return this;
    }

    /**
     * Adds a scalar expression.
     *
     * @param expression the scalar expression
     * @return this builder for method chaining
     */
    public MysqlCoalesceBuilder expression(ScalarExpression expression) {
        Objects.requireNonNull(expression, "Expression must not be null");
        expressions.add(expression);
        return this;
    }

    /**
     * Adds a literal value (will be converted to SQL literal).
     *
     * @param value the literal value
     * @return this builder for method chaining
     */
    public MysqlCoalesceBuilder literal(Object value) {
        Objects.requireNonNull(value, "Value must not be null");
        expressions.add(Literal.of(String.valueOf(value)));
        return this;
    }

    /**
     * Finalizes the COALESCE function with an alias and returns to the projection builder.
     *
     * @param alias the alias for the COALESCE result
     * @return the parent MysqlSelectProjectionBuilder for continued query building
     */
    public MysqlSelectProjectionBuilder as(String alias) {
        if (expressions.size() < 2) {
            throw new IllegalStateException("COALESCE requires at least two expressions");
        }

        CustomFunctionCall coalesceCall = new CustomFunctionCall("COALESCE", expressions, Map.of());
        return parent.expression(coalesceCall).as(alias);
    }
}
