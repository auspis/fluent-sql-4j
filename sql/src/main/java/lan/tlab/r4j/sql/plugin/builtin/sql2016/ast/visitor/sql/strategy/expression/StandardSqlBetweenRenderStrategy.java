package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.Between;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.BetweenRenderStrategy;

public class StandardSqlBetweenRenderStrategy implements BetweenRenderStrategy {

    @Override
    public String render(Between expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s BETWEEN %s AND %s)",
                expression.testExpression().accept(sqlRenderer, ctx),
                expression.startExpression().accept(sqlRenderer, ctx),
                expression.endExpression().accept(sqlRenderer, ctx));
    }
}
