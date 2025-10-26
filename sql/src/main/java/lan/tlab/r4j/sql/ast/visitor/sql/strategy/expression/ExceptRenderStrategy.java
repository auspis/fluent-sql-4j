package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface ExceptRenderStrategy extends ExpressionRenderStrategy {

    String render(ExceptExpression expression, SqlRenderer sqlRenderer, AstContext ctx);

    public static ExceptRenderStrategy standardSql2008() {
        return (expression, sqlRenderer, ctx) -> String.format(
                "((%s) %s (%s))",
                expression.left().accept(sqlRenderer, ctx),
                expression.distinct() ? "EXCEPT" : "EXCEPT ALL",
                expression.right().accept(sqlRenderer, ctx));
    }

    public static ExceptRenderStrategy oracle() {
        return (expression, sqlRenderer, ctx) -> String.format(
                "((%s) MINUS (%s))",
                expression.left().accept(sqlRenderer, ctx), expression.right().accept(sqlRenderer, ctx));
    }
}
