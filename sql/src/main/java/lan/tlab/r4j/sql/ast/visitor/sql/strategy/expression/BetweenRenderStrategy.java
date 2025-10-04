package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.Between;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class BetweenRenderStrategy implements ExpressionRenderStrategy {

    public String render(Between expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s BETWEEN %s AND %s)",
                expression.getTestExpression().accept(sqlRenderer, ctx),
                expression.getStartExpression().accept(sqlRenderer, ctx),
                expression.getEndExpression().accept(sqlRenderer, ctx));
    }
}
