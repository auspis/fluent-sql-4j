package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.Between;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class StandardSqlBetweenRenderStrategy implements ExpressionRenderStrategy {

    public String render(Between expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s BETWEEN %s AND %s)",
                expression.testExpression().accept(sqlRenderer, ctx),
                expression.startExpression().accept(sqlRenderer, ctx),
                expression.endExpression().accept(sqlRenderer, ctx));
    }
}
