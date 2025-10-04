package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface ExceptRenderStrategy extends ExpressionRenderStrategy {

    String render(ExceptExpression expression, SqlRenderer sqlRenderer, AstContext ctx);

    public static ExceptRenderStrategy standardSql2008() {
        return (expression, sqlRenderer, ctx) -> String.format(
                "((%s) %s (%s))",
                expression.getLeft().accept(sqlRenderer, ctx),
                expression.isDistinct() ? "EXCEPT" : "EXCEPT ALL",
                expression.getRight().accept(sqlRenderer, ctx));
    }

    public static ExceptRenderStrategy oracle() {
        return (expression, sqlRenderer, ctx) -> String.format(
                "((%s) MINUS (%s))",
                expression.getLeft().accept(sqlRenderer, ctx),
                expression.getRight().accept(sqlRenderer, ctx));
    }
}
