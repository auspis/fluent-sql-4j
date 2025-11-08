package lan.tlab.r4j.sql.dsl.select;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.dql.projection.Projection;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;

public class SelectProjectionBuilder {
    private final DialectRenderer renderer;
    private final List<Projection> projections;
    private Projection pendingProjection;

    public SelectProjectionBuilder(DialectRenderer renderer) {
        this.renderer = renderer;
        this.projections = new ArrayList<>();
        this.pendingProjection = null;
    }

    public SelectProjectionBuilder column(String column) {
        return column("", column);
    }

    public SelectProjectionBuilder column(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new ScalarExpressionProjection(ColumnReference.of(table, column));
        return this;
    }

    public SelectProjectionBuilder expression(ScalarExpression expression) {
        finalizePendingProjection();
        pendingProjection = new ScalarExpressionProjection(expression);
        return this;
    }

    public SelectProjectionBuilder expression(ScalarExpression expression, String alias) {
        finalizePendingProjection();
        pendingProjection = new ScalarExpressionProjection(expression, alias);
        return this;
    }

    public SelectProjectionBuilder sum(String column) {
        return sum("", column);
    }

    public SelectProjectionBuilder sum(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.sum(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder avg(String column) {
        return avg("", column);
    }

    public SelectProjectionBuilder avg(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.avg(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder count(String column) {
        return count("", column);
    }

    public SelectProjectionBuilder count(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.count(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder countStar() {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.countStar());
        return this;
    }

    public SelectProjectionBuilder countDistinct(String column) {
        return countDistinct("", column);
    }

    public SelectProjectionBuilder countDistinct(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.countDistinct(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder max(String column) {
        return max("", column);
    }

    public SelectProjectionBuilder max(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.max(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder min(String column) {
        return min("", column);
    }

    public SelectProjectionBuilder min(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.min(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder as(String alias) {
        if (pendingProjection == null) {
            throw new IllegalStateException("No projection to alias. Call an aggregate or column method first.");
        }

        // Replace pending projection with aliased version
        if (pendingProjection instanceof AggregateCallProjection aggProj) {
            pendingProjection = new AggregateCallProjection((AggregateCall) aggProj.expression(), alias);
        } else if (pendingProjection instanceof ScalarExpressionProjection scalarProj) {
            pendingProjection = new ScalarExpressionProjection((ScalarExpression) scalarProj.expression(), alias);
        }

        return this;
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
    public JsonFunctionBuilder jsonExists(String column, String path) {
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
    public JsonFunctionBuilder jsonExists(String table, String column, String path) {
        finalizePendingProjection();
        return new JsonFunctionBuilder(this, table, column, path, JsonFunctionBuilder.JsonFunctionType.EXISTS);
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
    public JsonFunctionBuilder jsonValue(String column, String path) {
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
    public JsonFunctionBuilder jsonValue(String table, String column, String path) {
        finalizePendingProjection();
        return new JsonFunctionBuilder(this, table, column, path, JsonFunctionBuilder.JsonFunctionType.VALUE);
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
    public JsonFunctionBuilder jsonQuery(String column, String path) {
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
    public JsonFunctionBuilder jsonQuery(String table, String column, String path) {
        finalizePendingProjection();
        return new JsonFunctionBuilder(this, table, column, path, JsonFunctionBuilder.JsonFunctionType.QUERY);
    }

    public SelectBuilder from(String tableName) {
        finalizePendingProjection();

        if (projections.isEmpty()) {
            throw new IllegalStateException("At least one projection must be specified before calling from()");
        }

        Select select = Select.of(projections.toArray(new Projection[0]));
        SelectBuilder selectBuilder = new SelectBuilder(renderer, select);
        return selectBuilder.from(tableName);
    }

    private void finalizePendingProjection() {
        if (pendingProjection != null) {
            projections.add(pendingProjection);
            pendingProjection = null;
        }
    }
}
