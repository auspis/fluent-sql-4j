package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.bool.Between;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class BetweenRenderStrategy implements ExpressionRenderStrategy {

    public String render(Between expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s BETWEEN %s AND %s)",
                expression.getTestExpression().accept(sqlRenderer, ctx),
                expression.getStartExpression().accept(sqlRenderer, ctx),
                expression.getEndExpression().accept(sqlRenderer, ctx));
    }
}
