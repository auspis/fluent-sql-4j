package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.set.ExceptExpression;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface ExceptRenderStrategy extends ExpressionRenderStrategy {

    String render(ExceptExpression expression, SqlRenderer sqlRenderer);

    public static ExceptRenderStrategy standardSql2008() {
        return (expression, sqlRenderer) -> String.format(
                "((%s) %s (%s))",
                expression.getLeft().accept(sqlRenderer),
                expression.isDistinct() ? "EXCEPT" : "EXCEPT ALL",
                expression.getRight().accept(sqlRenderer));
    }

    public static ExceptRenderStrategy oracle() {
        return (expression, sqlRenderer) -> String.format(
                "((%s) MINUS (%s))",
                expression.getLeft().accept(sqlRenderer), expression.getRight().accept(sqlRenderer));
    }
}
