package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.bool.Between;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class BetweenRenderStrategy implements ExpressionRenderStrategy {

    public String render(Between expression, SqlRenderer sqlRenderer) {
        return String.format(
                "(%s BETWEEN %s AND %s)",
                expression.getTestExpression().accept(sqlRenderer),
                expression.getStartExpression().accept(sqlRenderer),
                expression.getEndExpression().accept(sqlRenderer));
    }
}
