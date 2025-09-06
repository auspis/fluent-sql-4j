package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.bool.IsNull;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class IsNullRenderStrategy implements ExpressionRenderStrategy {

    public String render(IsNull expression, SqlRenderer sqlRenderer) {
        return String.format("%s IS NULL", expression.getExpression().accept(sqlRenderer));
    }
}
