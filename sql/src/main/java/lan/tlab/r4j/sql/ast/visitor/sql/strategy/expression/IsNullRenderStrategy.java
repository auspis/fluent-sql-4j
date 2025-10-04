package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.bool.IsNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class IsNullRenderStrategy implements ExpressionRenderStrategy {

    public String render(IsNull expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s IS NULL", expression.getExpression().accept(sqlRenderer, ctx));
    }
}
