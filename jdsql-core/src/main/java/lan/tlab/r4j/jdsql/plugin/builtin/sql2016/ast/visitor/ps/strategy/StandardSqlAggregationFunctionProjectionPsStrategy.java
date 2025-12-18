package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.EscapeStrategy;

public class StandardSqlAggregationFunctionProjectionPsStrategy implements AggregationFunctionProjectionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            AggregateExpressionProjection aggregateExpressionProjection,
            Visitor<PreparedStatementSpec> renderer,
            AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();
        if (renderer instanceof AstToPreparedStatementSpecVisitor psRenderer) {
            escapeStrategy = psRenderer.getEscapeStrategy();
        }

        // The AggregateExpressionProjection wraps an AggregateExpression (e.g., COUNT, SUM, etc.)
        var expr = aggregateExpressionProjection.expression();
        PreparedStatementSpec exprResult = expr.accept(renderer, ctx);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = aggregateExpressionProjection.as() != null
                ? aggregateExpressionProjection.as().name()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + escapeStrategy.apply(alias);
        }
        return new PreparedStatementSpec(sql, exprResult.parameters());
    }
}
