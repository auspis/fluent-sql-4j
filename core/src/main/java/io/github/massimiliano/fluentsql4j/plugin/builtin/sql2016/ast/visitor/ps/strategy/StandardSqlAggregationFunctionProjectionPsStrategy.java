package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.projection.AggregateExpressionProjection;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;

public class StandardSqlAggregationFunctionProjectionPsStrategy implements AggregationFunctionProjectionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            AggregateExpressionProjection aggregateExpressionProjection,
            Visitor<PreparedStatementSpec> renderer,
            AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();
        if (renderer instanceof AstToPreparedStatementSpecVisitor astToPsSpecVisitor) {
            escapeStrategy = astToPsSpecVisitor.getEscapeStrategy();
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
