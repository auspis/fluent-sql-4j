package io.github.auspis.fluentsql4j.dsl.select;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.window.DenseRank;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Lag;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Lead;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Ntile;
import io.github.auspis.fluentsql4j.ast.core.expression.window.OverClause;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Rank;
import io.github.auspis.fluentsql4j.ast.core.expression.window.RowNumber;
import io.github.auspis.fluentsql4j.ast.core.expression.window.WindowFunction;
import io.github.auspis.fluentsql4j.ast.dql.clause.Sorting;
import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for window functions in SELECT projections.
 * <p>
 * This builder provides a convenient API for creating window functions with OVER clauses,
 * supporting PARTITION BY and ORDER BY specifications.
 * <p>
 * Example usage:
 * <pre>{@code
 * dsl.select()
 *     .rowNumber()
 *         .partitionBy("department")
 *         .orderBy("salary", "DESC")
 *         .as("rank")
 *     .from("employees")
 * }</pre>
 *
 * @param <PARENT> the type of the parent projection builder
 * @see WindowFunction
 * @see OverClause
 */
// TODO: separate builders for different window functions (see fields and constructors)
public class WindowFunctionBuilder<PARENT extends SelectProjectionBuilder<PARENT>> {

    /**
     * Types of window functions supported by the builder.
     */
    public enum WindowFunctionType {
        ROW_NUMBER,
        RANK,
        DENSE_RANK,
        NTILE,
        LAG,
        LEAD
    }

    private final PARENT projectionBuilder;
    private final String defaultTableReference;
    private final WindowFunctionType functionType;

    // Function-specific parameters
    private Integer ntileBuckets;
    private ScalarExpression lagLeadExpression;
    private Integer lagLeadOffset;
    private ScalarExpression lagLeadDefaultValue;

    // OVER clause components
    private final List<ColumnReference> partitionByColumns = new ArrayList<>();
    private final List<Sorting> orderByList = new ArrayList<>();

    /**
     * Creates a window function builder for ROW_NUMBER, RANK, or DENSE_RANK.
     */
    WindowFunctionBuilder(PARENT projectionBuilder, String defaultTableReference, WindowFunctionType functionType) {
        this.projectionBuilder = projectionBuilder;
        this.defaultTableReference = defaultTableReference;
        this.functionType = functionType;
    }

    /**
     * Creates a window function builder for NTILE.
     */
    WindowFunctionBuilder(
            PARENT projectionBuilder, String defaultTableReference, WindowFunctionType functionType, int buckets) {
        this(projectionBuilder, defaultTableReference, functionType);
        this.ntileBuckets = buckets;
    }

    /**
     * Creates a window function builder for LAG or LEAD.
     */
    WindowFunctionBuilder(
            PARENT projectionBuilder,
            String defaultTableReference,
            WindowFunctionType functionType,
            String column,
            int offset) {
        this(projectionBuilder, defaultTableReference, functionType);
        this.lagLeadExpression = toDefaultColumnReference(
                column,
                "LAG/LEAD column cannot be null or empty",
                "LAG/LEAD column must not contain dot notation. Use lag(table, column, offset) or lead(table, column, offset) with separate parameters");
        this.lagLeadOffset = offset;
    }

    /**
     * Creates a window function builder for LAG or LEAD with table reference.
     */
    WindowFunctionBuilder(
            PARENT projectionBuilder,
            String defaultTableReference,
            WindowFunctionType functionType,
            String table,
            String column,
            int offset) {
        this(projectionBuilder, defaultTableReference, functionType);
        this.lagLeadExpression = toQualifiedColumnReference(
                table,
                column,
                "LAG/LEAD table cannot be null or empty",
                "LAG/LEAD column cannot be null or empty",
                "LAG/LEAD table must not contain dot notation: '",
                "LAG/LEAD column must not contain dot notation: '");
        this.lagLeadOffset = offset;
    }

    /**
     * Adds PARTITION BY clause with a single column.
     *
     * @param column the column name to partition by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder<PARENT> partitionBy(String column) {
        partitionByColumns.add(
                toDefaultColumnReference(
                        column,
                        "PARTITION BY column cannot be null or empty",
                        "PARTITION BY column must not contain dot notation. Use partitionBy(table, column) with separate parameters"));
        return this;
    }

    /**
     * Adds PARTITION BY clause with explicit table reference.
     *
     * @param table the table name
     * @param column the column name to partition by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder<PARENT> partitionBy(String table, String column) {
        partitionByColumns.add(
                toQualifiedColumnReference(
                        table,
                        column,
                        "PARTITION BY table cannot be null or empty",
                        "PARTITION BY column cannot be null or empty",
                        "PARTITION BY table must not contain dot notation: '",
                        "PARTITION BY column must not contain dot notation. Use partitionBy(table, column) with separate parameters: '"));
        return this;
    }

    /**
     * Adds ORDER BY clause with ascending order.
     *
     * @param column the column name to order by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder<PARENT> orderByAsc(String column) {
        ColumnReference columnRef = toDefaultColumnReference(
                column,
                "ORDER BY column cannot be null or empty",
                "ORDER BY column must not contain dot notation. Use orderByAsc(table, column) with separate parameters");
        orderByList.add(Sorting.asc(columnRef));
        return this;
    }

    /**
     * Adds ORDER BY clause with descending order.
     *
     * @param column the column name to order by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder<PARENT> orderByDesc(String column) {
        ColumnReference columnRef = toDefaultColumnReference(
                column,
                "ORDER BY column cannot be null or empty",
                "ORDER BY column must not contain dot notation. Use orderByDesc(table, column) with separate parameters");
        orderByList.add(Sorting.desc(columnRef));
        return this;
    }

    /**
     * Adds ORDER BY clause with explicit table reference and ascending order.
     *
     * @param table the table name
     * @param column the column name to order by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder<PARENT> orderByAsc(String table, String column) {
        ColumnReference columnRef = toQualifiedColumnReference(
                table,
                column,
                "ORDER BY table cannot be null or empty",
                "ORDER BY column cannot be null or empty",
                "ORDER BY table must not contain dot notation: '",
                "ORDER BY column must not contain dot notation. Use orderByAsc(table, column) with separate parameters: '");
        orderByList.add(Sorting.asc(columnRef));
        return this;
    }

    /**
     * Adds ORDER BY clause with explicit table reference and descending order.
     *
     * @param table the table name
     * @param column the column name to order by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder<PARENT> orderByDesc(String table, String column) {
        ColumnReference columnRef = toQualifiedColumnReference(
                table,
                column,
                "ORDER BY table cannot be null or empty",
                "ORDER BY column cannot be null or empty",
                "ORDER BY table must not contain dot notation: '",
                "ORDER BY column must not contain dot notation. Use orderByDesc(table, column) with separate parameters: '");
        orderByList.add(Sorting.desc(columnRef));
        return this;
    }

    /**
     * Sets the default value for LAG/LEAD when the offset goes beyond the window.
     *
     * @param defaultValue the default value as a ScalarExpression
     * @return this builder for chaining
     */
    public WindowFunctionBuilder<PARENT> defaultValue(ScalarExpression defaultValue) {
        if (functionType != WindowFunctionType.LAG && functionType != WindowFunctionType.LEAD) {
            throw new IllegalStateException("defaultValue() can only be used with LAG or LEAD functions");
        }
        this.lagLeadDefaultValue = defaultValue;
        return this;
    }

    /**
     * Adds a regular column projection and finalizes the current window function without an alias.
     * <p>
     * This allows mixing window functions and regular columns in the SELECT clause.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .column("name")
     *     .rowNumber().partitionBy("dept").column("salary")  // finalize window function
     *     .from("employees")
     * }</pre>
     *
     * @param column the column name to add
     * @return SelectProjectionBuilder for continued query building
     */
    public PARENT column(String column) {
        finalizeCurrentWindowFunction();
        return projectionBuilder.column(column);
    }

    /**
     * Adds a regular column projection with table reference and finalizes the current window function.
     *
     * @param table the table name
     * @param column the column name to add
     * @return SelectProjectionBuilder for continued query building
     */
    public PARENT column(String table, String column) {
        finalizeCurrentWindowFunction();
        return projectionBuilder.column(table, column);
    }

    /**
     * Finalizes the window function with an alias and returns to the projection builder.
     *
     * @param alias the alias for this window function
     * @return the SelectProjectionBuilder for continued query building
     */
    public PARENT as(String alias) {
        WindowFunction windowFunction = buildWindowFunction();
        return projectionBuilder.expression(windowFunction, alias);
    }

    /**
     * Finalizes the current window function without an alias.
     * This is used internally when switching to another projection type.
     */
    private void finalizeCurrentWindowFunction() {
        WindowFunction windowFunction = buildWindowFunction();
        projectionBuilder.expression(windowFunction);
    }

    /**
     * Builds the WindowFunction AST node based on the configured parameters.
     */
    private WindowFunction buildWindowFunction() {
        OverClause.Builder overBuilder = OverClause.builder();

        // Add PARTITION BY if specified
        if (!partitionByColumns.isEmpty()) {
            if (partitionByColumns.size() == 1) {
                overBuilder.partitionBy(partitionByColumns.get(0));
            } else {
                overBuilder.partitionBy(partitionByColumns.toArray(new ColumnReference[0]));
            }
        }

        // Add ORDER BY if specified
        if (!orderByList.isEmpty()) {
            overBuilder.orderBy(orderByList);
        }

        OverClause overClause = overBuilder.build();

        // Build the appropriate window function based on type
        return switch (functionType) {
            case ROW_NUMBER -> new RowNumber(overClause);
            case RANK -> new Rank(overClause);
            case DENSE_RANK -> new DenseRank(overClause);
            case NTILE -> new Ntile(ntileBuckets, overClause);
            case LAG -> new Lag(lagLeadExpression, lagLeadOffset, lagLeadDefaultValue, overClause);
            case LEAD -> new Lead(lagLeadExpression, lagLeadOffset, lagLeadDefaultValue, overClause);
        };
    }

    /**
     * Parses a column reference, handling both "table.column" and "column" formats.
     */
    private ColumnReference toDefaultColumnReference(String column, String nullOrEmptyMessage, String dotMessage) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException(nullOrEmptyMessage);
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException(dotMessage + ": '" + column + "'");
        }
        return ColumnReference.of(defaultTableReference, column);
    }

    private ColumnReference toQualifiedColumnReference(
            String table,
            String column,
            String tableNullOrEmptyMessage,
            String columnNullOrEmptyMessage,
            String tableDotMessagePrefix,
            String columnDotMessagePrefix) {
        if (table == null || table.trim().isEmpty()) {
            throw new IllegalArgumentException(tableNullOrEmptyMessage);
        }
        if (table.contains(".")) {
            throw new IllegalArgumentException(tableDotMessagePrefix + table + "'");
        }
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException(columnNullOrEmptyMessage);
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException(columnDotMessagePrefix + column + "'");
        }
        return ColumnReference.of(table, column);
    }
}
