package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class LikeRenderStrategy implements ExpressionRenderStrategy {

    public String render(Like expression, SqlRenderer sqlRenderer) {
        return String.format("%s LIKE '%s'", expression.getExpression().accept(sqlRenderer), expression.getPattern());
    }
}
