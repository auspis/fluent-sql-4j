package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.set.IntersectExpression;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class IntersectRenderStrategy implements ExpressionRenderStrategy {

    public String render(IntersectExpression expression, SqlRenderer sqlRenderer) {
        return String.format(
                "((%s) %s (%s))",
                expression.getLeftSetExpression().accept(sqlRenderer),
                expression.getType().equals(IntersectExpression.IntersectType.INTERSECT_ALL)
                        ? "INTERSECT ALL"
                        : "INTERSECT",
                expression.getRightSetExpression().accept(sqlRenderer));
    }
}
