package lan.tlab.r4j.jdsql.dsl.clause;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;

/**
 * Builder for WHERE clause that supports both regular column conditions and JSON functions.
 * <p>
 * This builder provides a fluent API for constructing WHERE predicates, including:
 * <ul>
 *   <li>Regular column comparisons via {@link WhereConditionBuilder}</li>
 *   <li>JSON function predicates via {@link WhereJsonFunctionBuilder}</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>{@code
 * dsl.select("*")
 *     .from("users")
 *     .where()
 *     .column("name").eq("John")
 *     .and()
 *     .jsonValue("info", "$.city").eq("Rome")
 *     .build();
 * }</pre>
 */
public class WhereBuilder<T extends SupportsWhere<T>> {

    private final T parent;
    private final LogicalCombinator combinator;

    public WhereBuilder(T parent, LogicalCombinator combinator) {
        this.parent = parent;
        this.combinator = combinator;
    }

    /**
     * Start a condition on a regular column using the parent's table reference.
     * <p>
     * This method is suitable for single-table queries.
     *
     * @param column the column name (must not contain dot notation)
     * @return a condition builder for the column
     * @throws IllegalArgumentException if column is null, empty, or contains dot notation
     */
    public WhereConditionBuilder<T> column(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException(
                    "Dot notation not supported. Use column(alias, column) for qualified references");
        }
        return new WhereConditionBuilder<>(parent, column, combinator);
    }

    /**
     * Start a condition on a column with explicit table alias.
     * <p>
     * This method is suitable for multi-table queries (e.g., with JOINs) where you need to
     * reference columns from specific tables.
     * <p>
     * Example:
     * <pre>{@code
     * .where()
     * .column("u", "age").gt(18)
     * .and()
     * .column("o", "status").eq("COMPLETED")
     * }</pre>
     *
     * @param alias the table alias (must not contain dot notation)
     * @param column the column name (must not contain dot notation)
     * @return a condition builder for the column
     * @throws IllegalArgumentException if alias or column is null, empty, or contains dot notation
     */
    public WhereConditionBuilder<T> column(String alias, String column) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        if (alias.contains(".")) {
            throw new IllegalArgumentException("Alias must not contain dot: '" + alias + "'");
        }
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException(
                    "Column name must not contain dot. Use column(alias, column) with separate parameters");
        }
        ColumnReference colRef = ColumnReference.of(alias, column);
        return new WhereConditionBuilder<>(parent, colRef, combinator);
    }

    /**
     * Start a JSON_EXISTS condition.
     * <p>
     * Example:
     * <pre>{@code
     * .where()
     * .jsonExists("info", "$.email").exists()
     * }</pre>
     *
     * @param column the column containing JSON data
     * @param path the JSON path expression
     * @return a JSON function builder
     */
    public WhereJsonFunctionBuilder<T> jsonExists(String column, String path) {
        return jsonExists("", column, path);
    }

    /**
     * Start a JSON_EXISTS condition with table reference.
     *
     * @param table the table reference
     * @param column the column containing JSON data
     * @param path the JSON path expression
     * @return a JSON function builder
     */
    public WhereJsonFunctionBuilder<T> jsonExists(String table, String column, String path) {
        return new WhereJsonFunctionBuilder<>(
                parent, table, column, path, WhereJsonFunctionBuilder.JsonFunctionType.EXISTS, combinator);
    }

    /**
     * Start a JSON_VALUE condition.
     * <p>
     * Example:
     * <pre>{@code
     * .where()
     * .jsonValue("info", "$.city").eq("Rome")
     * }</pre>
     *
     * @param column the column containing JSON data
     * @param path the JSON path expression
     * @return a JSON function builder
     */
    public WhereJsonFunctionBuilder<T> jsonValue(String column, String path) {
        return jsonValue("", column, path);
    }

    /**
     * Start a JSON_VALUE condition with table reference.
     *
     * @param table the table reference
     * @param column the column containing JSON data
     * @param path the JSON path expression
     * @return a JSON function builder
     */
    public WhereJsonFunctionBuilder<T> jsonValue(String table, String column, String path) {
        return new WhereJsonFunctionBuilder<>(
                parent, table, column, path, WhereJsonFunctionBuilder.JsonFunctionType.VALUE, combinator);
    }

    /**
     * Start a JSON_QUERY condition.
     * <p>
     * Example:
     * <pre>{@code
     * .where()
     * .jsonQuery("info", "$.tags").isNotNull()
     * }</pre>
     *
     * @param column the column containing JSON data
     * @param path the JSON path expression
     * @return a JSON function builder
     */
    public WhereJsonFunctionBuilder<T> jsonQuery(String column, String path) {
        return jsonQuery("", column, path);
    }

    /**
     * Start a JSON_QUERY condition with table reference.
     *
     * @param table the table reference
     * @param column the column containing JSON data
     * @param path the JSON path expression
     * @return a JSON function builder
     */
    public WhereJsonFunctionBuilder<T> jsonQuery(String table, String column, String path) {
        return new WhereJsonFunctionBuilder<>(
                parent, table, column, path, WhereJsonFunctionBuilder.JsonFunctionType.QUERY, combinator);
    }
}
