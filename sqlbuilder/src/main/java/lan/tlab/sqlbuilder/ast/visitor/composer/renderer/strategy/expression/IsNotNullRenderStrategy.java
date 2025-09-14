package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class IsNotNullRenderStrategy implements ExpressionRenderStrategy {

    public String render(IsNotNull expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s IS NOT NULL", expression.getExpression().accept(sqlRenderer, ctx));
    }
}
