package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.Expression;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ScalarExpressionProjectionPsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class StandardSqlScalarExpressionProjectionPsStrategy implements ScalarExpressionProjectionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            ScalarExpressionProjection scalarExpressionProjection,
            Visitor<PreparedStatementSpec> renderer,
            AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();
        if (renderer instanceof PreparedStatementRenderer psRenderer) {
            escapeStrategy = psRenderer.getEscapeStrategy();
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
