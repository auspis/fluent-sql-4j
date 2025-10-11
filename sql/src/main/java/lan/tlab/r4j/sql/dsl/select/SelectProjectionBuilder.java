package lan.tlab.r4j.sql.dsl.select;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.clause.selection.projection.Projection;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class SelectProjectionBuilder {
    private final SqlRenderer sqlRenderer;
    private final List<Projection> projections;
    private Projection pendingProjection;

    public SelectProjectionBuilder(SqlRenderer sqlRenderer) {
        this.sqlRenderer = sqlRenderer;
        this.projections = new ArrayList<>();
        this.pendingProjection = null;
    }

    public SelectProjectionBuilder column(String column) {
        finalizePendingProjection();
        pendingProjection = new ScalarExpressionProjection(ColumnReference.of("", column));
        return this;
    }

    public SelectProjectionBuilder column(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new ScalarExpressionProjection(ColumnReference.of(table, column));
        return this;
    }

    public SelectProjectionBuilder sum(String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.sum(ColumnReference.of("", column)));
        return this;
    }

    public SelectProjectionBuilder sum(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.sum(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder avg(String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.avg(ColumnReference.of("", column)));
        return this;
    }

    public SelectProjectionBuilder avg(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.avg(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder count(String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.count(ColumnReference.of("", column)));
        return this;
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
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.countDistinct(ColumnReference.of("", column)));
        return this;
    }

    public SelectProjectionBuilder countDistinct(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.countDistinct(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder max(String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.max(ColumnReference.of("", column)));
        return this;
    }

    public SelectProjectionBuilder max(String table, String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.max(ColumnReference.of(table, column)));
        return this;
    }

    public SelectProjectionBuilder min(String column) {
        finalizePendingProjection();
        pendingProjection = new AggregateCallProjection(AggregateCall.min(ColumnReference.of("", column)));
        return this;
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
            pendingProjection = new AggregateCallProjection((AggregateCall) aggProj.getExpression(), alias);
        } else if (pendingProjection instanceof ScalarExpressionProjection scalarProj) {
            pendingProjection = new ScalarExpressionProjection(
                    (lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression) scalarProj.getExpression(), alias);
        }

        return this;
    }

    public SelectBuilder from(String tableName) {
        finalizePendingProjection();

        if (projections.isEmpty()) {
            throw new IllegalStateException("At least one projection must be specified before calling from()");
        }

        Select select = Select.of(projections.toArray(new Projection[0]));
        SelectBuilder selectBuilder = new SelectBuilder(sqlRenderer, select);
        return selectBuilder.from(tableName);
    }

    private void finalizePendingProjection() {
        if (pendingProjection != null) {
            projections.add(pendingProjection);
            pendingProjection = null;
        }
    }
}
