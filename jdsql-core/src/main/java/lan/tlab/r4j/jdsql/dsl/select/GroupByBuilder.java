package lan.tlab.r4j.jdsql.dsl.select;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.dsl.clause.HavingBuilder;

/**
 * Fluent builder for GROUP BY clauses with support for both single-table and multi-table contexts.
 *
 * <p>Supports explicit table aliases for multi-table queries (JOINs) and simple column names
 * for single-table queries.
 *
 * <p>Example usage:
 * <pre>{@code
 * dsl.select()
 *     .column("u", "country")
 *     .column("o", "status")
 *     .sum("o", "total").as("total_sales")
 *     .from("users").as("u")
 *     .innerJoin("orders").as("o").on("u", "id", "o", "user_id")
 *     .groupBy()
 *         .column("country")         // From base table (users)
 *         .column("o", "status")     // From joined table
 *     .orderBy("total_sales")
 *     .build(connection);
 * }</pre>
 */
public class GroupByBuilder {
    private final SelectBuilder parent;
    private final List<ColumnReference> columns;

    public GroupByBuilder(SelectBuilder parent) {
        this.parent = parent;
        this.columns = new ArrayList<>();
    }

    /**
     * Add a column from the base table to the GROUP BY clause.
     *
     * @param column the column name (simple name, no table qualifier)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if column is null, empty, or contains dot notation
     */
    public GroupByBuilder column(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("GROUP BY column cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException("GROUP BY column must not contain dot notation. "
                    + "Use column(alias, column) for qualified references: '"
                    + column
                    + "'");
        }
        columns.add(ColumnReference.of(parent.getTableReference(), column));
        return this;
    }

    /**
     * Add a column with explicit table alias to the GROUP BY clause.
     *
     * <p>Use this for multi-table queries where you need to specify which table the column belongs to.
     *
     * @param alias the table alias
     * @param column the column name
     * @return this builder for method chaining
     * @throws IllegalArgumentException if alias or column is null, empty, or contains dot notation
     */
    public GroupByBuilder column(String alias, String column) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("GROUP BY alias cannot be null or empty");
        }
        if (alias.contains(".")) {
            throw new IllegalArgumentException("GROUP BY alias must not contain dot notation: '" + alias + "'");
        }
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("GROUP BY column cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException("GROUP BY column must not contain dot notation. "
                    + "Use column(alias, column) with separate parameters: '"
                    + column
                    + "'");
        }
        columns.add(ColumnReference.of(alias, column));
        return this;
    }

    /**
     * Complete the GROUP BY clause and return to the parent SelectBuilder.
     *
     * <p>Internal method - use delegation methods (having, orderBy, fetch, offset, build(Connection)) for fluent API.
     *
     * @return the parent SelectBuilder for continued query building
     * @throws IllegalStateException if no columns were added
     */
    SelectBuilder build() {
        if (columns.isEmpty()) {
            throw new IllegalStateException("GROUP BY must contain at least one column");
        }
        return parent.updateGroupBy(GroupBy.of(columns.toArray(new ColumnReference[0])));
    }

    /**
     * Complete the GROUP BY clause and build the prepared statement with parameters bound.
     *
     * <p>This is the terminal operation for the fluent GROUP BY builder chain.
     *
     * @param connection the database connection used to create the PreparedStatement
     * @return a PreparedStatement with all parameters bound
     * @throws SQLException if an error occurs while creating the PreparedStatement
     * @throws IllegalStateException if no columns were added
     */
    public java.sql.PreparedStatement build(java.sql.Connection connection) throws java.sql.SQLException {
        return build().build(connection);
    }

    /**
     * Alias for {@link #build()} to maintain fluent method chaining without explicit build call.
     * Automatically invoked when transitioning to other clauses (HAVING, ORDER BY, etc.).
     *
     * @return a HavingBuilder to build the HAVING clause
     */
    public HavingBuilder having() {
        return build().having();
    }

    /**
     * Alias for {@link #build()} to maintain fluent method chaining.
     *
     * @return an OrderByBuilder to build the ORDER BY clause
     */
    public OrderByBuilder orderBy() {
        return build().orderBy();
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
