package lan.tlab.r4j.sql.dsl.select;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.DenseRank;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.Lag;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.Lead;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.Ntile;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.OverClause;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.Rank;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.RowNumber;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.WindowFunction;
import lan.tlab.r4j.sql.ast.dql.clause.Sorting;

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
 * @see WindowFunction
 * @see OverClause
 */
public class WindowFunctionBuilder {

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

    private final SelectProjectionBuilder projectionBuilder;
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
    WindowFunctionBuilder(
            SelectProjectionBuilder projectionBuilder, String defaultTableReference, WindowFunctionType functionType) {
        this.projectionBuilder = projectionBuilder;
        this.defaultTableReference = defaultTableReference;
        this.functionType = functionType;
    }

    /**
     * Creates a window function builder for NTILE.
     */
    WindowFunctionBuilder(
            SelectProjectionBuilder projectionBuilder,
            String defaultTableReference,
            WindowFunctionType functionType,
            int buckets) {
        this(projectionBuilder, defaultTableReference, functionType);
        this.ntileBuckets = buckets;
    }

    /**
     * Creates a window function builder for LAG or LEAD.
     */
    WindowFunctionBuilder(
            SelectProjectionBuilder projectionBuilder,
            String defaultTableReference,
            WindowFunctionType functionType,
            String column,
            int offset) {
        this(projectionBuilder, defaultTableReference, functionType);
        this.lagLeadExpression = parseColumnReference(column);
        this.lagLeadOffset = offset;
    }

    /**
     * Creates a window function builder for LAG or LEAD with table reference.
     */
    WindowFunctionBuilder(
            SelectProjectionBuilder projectionBuilder,
            String defaultTableReference,
            WindowFunctionType functionType,
            String table,
            String column,
            int offset) {
        this(projectionBuilder, defaultTableReference, functionType);
        this.lagLeadExpression = ColumnReference.of(table, column);
        this.lagLeadOffset = offset;
    }

    /**
     * Adds PARTITION BY clause with a single column.
     *
     * @param column the column name to partition by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder partitionBy(String column) {
        partitionByColumns.add(parseColumnReference(column));
        return this;
    }

    /**
     * Adds PARTITION BY clause with explicit table reference.
     *
     * @param table the table name
     * @param column the column name to partition by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder partitionBy(String table, String column) {
        partitionByColumns.add(ColumnReference.of(table, column));
        return this;
    }

    /**
     * Adds ORDER BY clause with ascending order.
     *
     * @param column the column name to order by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder orderByAsc(String column) {
        ColumnReference columnRef = parseColumnReference(column);
        orderByList.add(Sorting.asc(columnRef));
        return this;
    }

    /**
     * Adds ORDER BY clause with descending order.
     *
     * @param column the column name to order by
     * @return this builder for chaining
     */
    public WindowFunctionBuilder orderByDesc(String column) {
        ColumnReference columnRef = parseColumnReference(column);
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
    public WindowFunctionBuilder orderByAsc(String table, String column) {
        ColumnReference columnRef = ColumnReference.of(table, column);
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
    public WindowFunctionBuilder orderByDesc(String table, String column) {
        ColumnReference columnRef = ColumnReference.of(table, column);
        orderByList.add(Sorting.desc(columnRef));
        return this;
    }

    /**
     * Sets the default value for LAG/LEAD when the offset goes beyond the window.
     *
     * @param defaultValue the default value as a ScalarExpression
     * @return this builder for chaining
     */
    public WindowFunctionBuilder defaultValue(ScalarExpression defaultValue) {
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
    public SelectProjectionBuilder column(String column) {
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
    public SelectProjectionBuilder column(String table, String column) {
        finalizeCurrentWindowFunction();
        return projectionBuilder.column(table, column);
    }

    /**
     * Finalizes the window function with an alias and returns to the projection builder.
     *
     * @param alias the alias for this window function
     * @return the SelectProjectionBuilder for continued query building
     */
    public SelectProjectionBuilder as(String alias) {
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
    private ColumnReference parseColumnReference(String column) {
        if (column.contains(".")) {
            String[] parts = column.split("\\.", 2);
            return ColumnReference.of(parts[0], parts[1]);
        }
        return ColumnReference.of(defaultTableReference, column);
    }
}
