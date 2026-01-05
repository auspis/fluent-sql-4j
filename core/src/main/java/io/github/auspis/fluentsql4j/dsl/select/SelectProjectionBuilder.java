package io.github.auspis.fluentsql4j.dsl.select;

import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.projection.AggregateCallProjection;
import io.github.auspis.fluentsql4j.ast.dql.projection.Projection;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import java.util.ArrayList;
import java.util.List;

public class SelectProjectionBuilder<SELF extends SelectProjectionBuilder<SELF>> {
    private final PreparedStatementSpecFactory specFactory;
    private final List<Projection> projections;
    private Projection pendingProjection;

    public SelectProjectionBuilder(PreparedStatementSpecFactory specFactory) {
        this.specFactory = specFactory;
        this.projections = new ArrayList<>();
        this.pendingProjection = null;
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    public SELF column(String column) {
        return column("", column);
    }

    public SELF column(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new ScalarExpressionProjection(ColumnReference.of(table, column));
        return self();
    }

    public SELF expression(ScalarExpression expression) {
        finalizePendingProjection();
        pendingProjection = new ScalarExpressionProjection(expression);
        return self();
    }

    public SELF expression(ScalarExpression expression, String alias) {
        finalizePendingProjection();
        pendingProjection = new ScalarExpressionProjection(expression, alias);
        return self();
    }

    public SELF sum(String column) {
        return sum("", column);
    }

    public SELF sum(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.sum(ColumnReference.of(table, column)));
        return self();
    }

    public SELF avg(String column) {
        return avg("", column);
    }

    public SELF avg(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.avg(ColumnReference.of(table, column)));
        return self();
    }

    public SELF count(String column) {
        return count("", column);
    }

    public SELF count(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.count(ColumnReference.of(table, column)));
        return self();
    }

    public SELF countStar() {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.countStar());
        return self();
    }

    public SELF countDistinct(String column) {
        return countDistinct("", column);
    }

    public SELF countDistinct(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.countDistinct(ColumnReference.of(table, column)));
        return self();
    }

    public SELF max(String column) {
        return max("", column);
    }

    public SELF max(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.max(ColumnReference.of(table, column)));
        return self();
    }

    public SELF min(String column) {
        return min("", column);
    }

    public SELF min(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.min(ColumnReference.of(table, column)));
        return self();
    }

    public SELF as(String alias) {
        if (pendingProjection == null) {
            throw new IllegalStateException("No projection to alias. Call an aggregate or column method first.");
        }

        // Replace pending projection with aliased version
        if (pendingProjection instanceof AggregateCallProjection aggProj) {
            pendingProjection = new AggregateCallProjection((AggregateCall) aggProj.expression(), alias);
        } else if (pendingProjection instanceof ScalarExpressionProjection scalarProj) {
            pendingProjection = new ScalarExpressionProjection((ScalarExpression) scalarProj.expression(), alias);
        }

        return self();
    }

    /**
     * Starts building a JSON_EXISTS function call.
     * <p>
     * JSON_EXISTS checks whether a JSON path expression returns any data.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .jsonExists("data", "$.email").as("has_email")
     *     .from("users")
     * }</pre>
     *
     * @param column the column containing the JSON document
     * @param path the JSON path to check
     * @return a JsonFunctionBuilder for further configuration
     */
    public JsonFunctionBuilder<SELF> jsonExists(String column, String path) {
        return jsonExists("", column, path);
    }

    /**
     * Starts building a JSON_EXISTS function call with explicit table reference.
     *
     * @param table the table name
     * @param column the column containing the JSON document
     * @param path the JSON path to check
     * @return a JsonFunctionBuilder for further configuration
     */
    public JsonFunctionBuilder<SELF> jsonExists(String table, String column, String path) {
        finalizePendingProjection();
        return new JsonFunctionBuilder<>(self(), table, column, path, JsonFunctionBuilder.JsonFunctionType.EXISTS);
    }

    /**
     * Starts building a JSON_VALUE function call.
     * <p>
     * JSON_VALUE extracts a scalar value from a JSON document.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .jsonValue("data", "$.price")
     *         .returning("DECIMAL(10,2)")
     *         .as("price")
     *     .from("products")
     * }</pre>
     *
     * @param column the column containing the JSON document
     * @param path the JSON path to extract
     * @return a JsonFunctionBuilder for further configuration
     */
    public JsonFunctionBuilder<SELF> jsonValue(String column, String path) {
        return jsonValue("", column, path);
    }

    /**
     * Starts building a JSON_VALUE function call with explicit table reference.
     *
     * @param table the table name
     * @param column the column containing the JSON document
     * @param path the JSON path to extract
     * @return a JsonFunctionBuilder for further configuration
     */
    public JsonFunctionBuilder<SELF> jsonValue(String table, String column, String path) {
        finalizePendingProjection();
        return new JsonFunctionBuilder<>(self(), table, column, path, JsonFunctionBuilder.JsonFunctionType.VALUE);
    }

    /**
     * Starts building a JSON_QUERY function call.
     * <p>
     * JSON_QUERY extracts a JSON object or array from a JSON document.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .jsonQuery("data", "$.items")
     *         .withWrapper()
     *         .as("items")
     *     .from("products")
     * }</pre>
     *
     * @param column the column containing the JSON document
     * @param path the JSON path to extract
     * @return a JsonFunctionBuilder for further configuration
     */
    public JsonFunctionBuilder<SELF> jsonQuery(String column, String path) {
        return jsonQuery("", column, path);
    }

    /**
     * Starts building a JSON_QUERY function call with explicit table reference.
     *
     * @param table the table name
     * @param column the column containing the JSON document
     * @param path the JSON path to extract
     * @return a JsonFunctionBuilder for further configuration
     */
    public JsonFunctionBuilder<SELF> jsonQuery(String table, String column, String path) {
        finalizePendingProjection();
        return new JsonFunctionBuilder<>(self(), table, column, path, JsonFunctionBuilder.JsonFunctionType.QUERY);
    }

    /**
     * Starts building a ROW_NUMBER window function.
     * <p>
     * ROW_NUMBER assigns a unique sequential integer to rows within a partition.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .rowNumber()
     *         .orderByDesc("salary")
     *         .as("rank")
     *     .from("employees")
     * }</pre>
     *
     * @return a WindowFunctionBuilder for further configuration
     */
    public WindowFunctionBuilder<SELF> rowNumber() {
        finalizePendingProjection();
        return new WindowFunctionBuilder<>(self(), "", WindowFunctionBuilder.WindowFunctionType.ROW_NUMBER);
    }

    /**
     * Starts building a RANK window function.
     * <p>
     * RANK assigns a rank to each row within a partition, with gaps in ranking for tied values.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .rank()
     *         .orderByDesc("score")
     *         .as("ranking")
     *     .from("scores")
     * }</pre>
     *
     * @return a WindowFunctionBuilder for further configuration
     */
    public WindowFunctionBuilder<SELF> rank() {
        finalizePendingProjection();
        return new WindowFunctionBuilder<>(self(), "", WindowFunctionBuilder.WindowFunctionType.RANK);
    }

    /**
     * Starts building a DENSE_RANK window function.
     * <p>
     * DENSE_RANK assigns a rank to each row within a partition, without gaps in ranking for tied values.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .denseRank()
     *         .partitionBy("category")
     *         .orderByDesc("price")
     *         .as("price_rank")
     *     .from("products")
     * }</pre>
     *
     * @return a WindowFunctionBuilder for further configuration
     */
    public WindowFunctionBuilder<SELF> denseRank() {
        finalizePendingProjection();
        return new WindowFunctionBuilder<>(self(), "", WindowFunctionBuilder.WindowFunctionType.DENSE_RANK);
    }

    /**
     * Starts building an NTILE window function.
     * <p>
     * NTILE divides rows into a specified number of approximately equal groups.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .ntile(4)
     *         .orderByDesc("revenue")
     *         .as("quartile")
     *     .from("sales")
     * }</pre>
     *
     * @param buckets the number of groups to divide rows into
     * @return a WindowFunctionBuilder for further configuration
     */
    public WindowFunctionBuilder<SELF> ntile(int buckets) {
        finalizePendingProjection();
        return new WindowFunctionBuilder<>(self(), "", WindowFunctionBuilder.WindowFunctionType.NTILE, buckets);
    }

    /**
     * Starts building a LAG window function.
     * <p>
     * LAG provides access to a row at a given physical offset prior to the current row.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .lag("amount", 1)
     *         .orderByAsc("date")
     *         .as("previous_amount")
     *     .from("transactions")
     * }</pre>
     *
     * @param column the column to retrieve from the previous row
     * @param offset the number of rows back from the current row
     * @return a WindowFunctionBuilder for further configuration
     */
    public WindowFunctionBuilder<SELF> lag(String column, int offset) {
        finalizePendingProjection();
        return new WindowFunctionBuilder<>(self(), "", WindowFunctionBuilder.WindowFunctionType.LAG, column, offset);
    }

    /**
     * Starts building a LAG window function with explicit table reference.
     *
     * @param table the table name
     * @param column the column to retrieve from the previous row
     * @param offset the number of rows back from the current row
     * @return a WindowFunctionBuilder for further configuration
     */
    public WindowFunctionBuilder<SELF> lag(String table, String column, int offset) {
        finalizePendingProjection();
        return new WindowFunctionBuilder<>(
                self(), "", WindowFunctionBuilder.WindowFunctionType.LAG, table, column, offset);
    }

    /**
     * Starts building a LEAD window function.
     * <p>
     * LEAD provides access to a row at a given physical offset following the current row.
     * <p>
     * Example:
     * <pre>{@code
     * dsl.select()
     *     .lead("amount", 1)
     *         .orderByAsc("date")
     *         .as("next_amount")
     *     .from("transactions")
     * }</pre>
     *
     * @param column the column to retrieve from the next row
     * @param offset the number of rows forward from the current row
     * @return a WindowFunctionBuilder for further configuration
     */
    public WindowFunctionBuilder<SELF> lead(String column, int offset) {
        finalizePendingProjection();
        return new WindowFunctionBuilder<>(self(), "", WindowFunctionBuilder.WindowFunctionType.LEAD, column, offset);
    }

    /**
     * Starts building a LEAD window function with explicit table reference.
     *
     * @param table the table name
     * @param column the column to retrieve from the next row
     * @param offset the number of rows forward from the current row
     * @return a WindowFunctionBuilder for further configuration
     */
    public WindowFunctionBuilder<SELF> lead(String table, String column, int offset) {
        finalizePendingProjection();
        return new WindowFunctionBuilder<>(
                self(), "", WindowFunctionBuilder.WindowFunctionType.LEAD, table, column, offset);
    }

    public SelectBuilder from(String tableName) {
        finalizePendingProjection();

        if (projections.isEmpty()) {
            throw new IllegalStateException("At least one projection must be specified before calling from()");
        }

        Select select = Select.of(projections.toArray(new Projection[0]));
        SelectBuilder selectBuilder = new SelectBuilder(specFactory, select);
        return selectBuilder.from(tableName);
    }

    protected void finalizePendingProjection() {
        if (pendingProjection != null) {
            projections.add(pendingProjection);
            pendingProjection = null;
        }
    }
}
