package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.Expression;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ScalarExpressionProjectionPsStrategy;

public class StandardSqlScalarExpressionProjectionPsStrategy implements ScalarExpressionProjectionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            ScalarExpressionProjection scalarExpressionProjection,
            Visitor<PreparedStatementSpec> renderer,
            AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();
        if (renderer instanceof AstToPreparedStatementSpecVisitor astToPsSpecVisitor) {
            escapeStrategy = astToPsSpecVisitor.getEscapeStrategy();
        }

        Expression expr = scalarExpressionProjection.expression();
        String alias = scalarExpressionProjection.as().name();

        PreparedStatementSpec exprResult = expr.accept(renderer, ctx);
        String sql = exprResult.sql();

        if (!alias.isBlank()) {
            sql += " AS " + escapeStrategy.apply(alias);
        }
        return new PreparedStatementSpec(sql, exprResult.parameters());
    }
}
