package lan.tlab.r4j.jdsql.dsl.select;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;

/**
 * Fluent builder for ORDER BY clauses with support for both single-table and multi-table contexts.
 *
 * <p>Supports explicit table aliases for multi-table queries (JOINs) and simple column names
 * for single-table queries. Each column can be sorted in ascending or descending order.
 *
 * <p>The order of method calls determines the SQL ORDER BY column order, which is semantically
 * significant in SQL.
 *
 * <p>Example usage:
 * <pre>{@code
 * dsl.select()
 *     .column("u", "name")
 *     .column("o", "total")
 *     .from("users").as("u")
 *     .innerJoin("orders").as("o").on("u", "id", "o", "user_id")
 *     .orderBy()
 *         .asc("u", "name")        // First sort: users.name ascending
 *         .desc("o", "total")      // Then sort: orders.total descending
 *     .build(connection);
 * }</pre>
 */
public class OrderByBuilder {
    private final SelectBuilder parent;
    private final List<Sorting> sortings;

    public OrderByBuilder(SelectBuilder parent) {
        this.parent = parent;
        this.sortings = new ArrayList<>();
    }

    /**
     * Add a column from the base table to the ORDER BY clause in ascending order.
     *
     * @param column the column name (simple name, no table qualifier)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if column is null, empty, or contains dot notation
     */
    public OrderByBuilder asc(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("ORDER BY column cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException("ORDER BY column must not contain dot notation. "
                    + "Use asc(alias, column) for qualified references: '"
                    + column
                    + "'");
        }
        sortings.add(Sorting.asc(ColumnReference.of(parent.getTableReference(), column)));
        return this;
    }

    /**
     * Add a column with explicit table alias to the ORDER BY clause in ascending order.
     *
     * <p>Use this for multi-table queries where you need to specify which table the column belongs to.
     *
     * @param alias the table alias
     * @param column the column name
     * @return this builder for method chaining
     * @throws IllegalArgumentException if alias or column is null, empty, or contains dot notation
     */
    public OrderByBuilder asc(String alias, String column) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("ORDER BY alias cannot be null or empty");
        }
        if (alias.contains(".")) {
            throw new IllegalArgumentException("ORDER BY alias must not contain dot notation: '" + alias + "'");
        }
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("ORDER BY column cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException("ORDER BY column must not contain dot notation. "
                    + "Use asc(alias, column) with separate parameters: '"
                    + column
                    + "'");
        }
        sortings.add(Sorting.asc(ColumnReference.of(alias, column)));
        return this;
    }

    /**
     * Add a column from the base table to the ORDER BY clause in descending order.
     *
     * @param column the column name (simple name, no table qualifier)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if column is null, empty, or contains dot notation
     */
    public OrderByBuilder desc(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("ORDER BY column cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException("ORDER BY column must not contain dot notation. "
                    + "Use desc(alias, column) for qualified references: '"
                    + column
                    + "'");
        }
        sortings.add(Sorting.desc(ColumnReference.of(parent.getTableReference(), column)));
        return this;
    }

    /**
     * Add a column with explicit table alias to the ORDER BY clause in descending order.
     *
     * <p>Use this for multi-table queries where you need to specify which table the column belongs to.
     *
     * @param alias the table alias
     * @param column the column name
     * @return this builder for method chaining
     * @throws IllegalArgumentException if alias or column is null, empty, or contains dot notation
     */
    public OrderByBuilder desc(String alias, String column) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("ORDER BY alias cannot be null or empty");
        }
        if (alias.contains(".")) {
            throw new IllegalArgumentException("ORDER BY alias must not contain dot notation: '" + alias + "'");
        }
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("ORDER BY column cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException("ORDER BY column must not contain dot notation. "
                    + "Use desc(alias, column) with separate parameters: '"
                    + column
                    + "'");
        }
        sortings.add(Sorting.desc(ColumnReference.of(alias, column)));
        return this;
    }

    /**
     * Complete the ORDER BY clause and return to the parent SelectBuilder.
     *
     * <p>Internal method - use delegation methods (fetch, offset, build(Connection)) for fluent API.
     *
     * @return the parent SelectBuilder for continued query building
     * @throws IllegalStateException if no sortings were added
     */
    SelectBuilder build() {
        if (sortings.isEmpty()) {
            throw new IllegalStateException("ORDER BY must contain at least one sorting column");
        }
        return parent.updateOrderBy(OrderBy.of(sortings.toArray(new Sorting[0])));
    }

    /**
     * Complete the ORDER BY clause and build the prepared statement with parameters bound.
     *
     * <p>This is a convenience method that chains both {@link #build()} (which completes the
     * ORDER BY clause and returns the parent SelectBuilder) and
     * {@link SelectBuilder#build(Connection)} (the terminal operation that creates the
     * PreparedStatement). This allows direct statement creation from the ORDER BY builder
     * without explicit intermediate steps.
     *
     * @param connection the database connection used to create the PreparedStatement
     * @return a PreparedStatement with all parameters bound
     * @throws SQLException if an error occurs while creating the PreparedStatement
     * @throws IllegalStateException if no sortings were added
     */
    public java.sql.PreparedStatement build(java.sql.Connection connection) throws java.sql.SQLException {
        return build().build(connection);
    }

    /**
     * Alias for {@link #build()} to maintain fluent method chaining.
     *
     * @return the parent SelectBuilder
     */
    public SelectBuilder fetch(int rows) {
        return build().fetch(rows);
    }

    /**
     * Alias for {@link #build()} to maintain fluent method chaining.
     *
     * @return the parent SelectBuilder
     */
    public SelectBuilder offset(int offset) {
        return build().offset(offset);
    }
}
