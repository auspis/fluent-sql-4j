package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class LikeRenderStrategy implements ExpressionRenderStrategy {

    public String render(Like expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s LIKE '%s'", expression.getExpression().accept(sqlRenderer, ctx), expression.getPattern());
    }
}
