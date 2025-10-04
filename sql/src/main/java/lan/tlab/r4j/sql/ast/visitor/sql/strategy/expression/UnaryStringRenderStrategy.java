package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.UnaryString;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class UnaryStringRenderStrategy implements ExpressionRenderStrategy {

    public String render(UnaryString functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s)",
                functionCall.getFunctionName(), functionCall.getExpression().accept(sqlRenderer, ctx));
    }
}
