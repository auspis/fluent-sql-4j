package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Left;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class LeftRenderStrategy implements ExpressionRenderStrategy {

    public String render(Left functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "LEFT(%s, %s)",
                functionCall.getExpression().accept(sqlRenderer, ctx),
                functionCall.getLength().accept(sqlRenderer, ctx));
    }
}
