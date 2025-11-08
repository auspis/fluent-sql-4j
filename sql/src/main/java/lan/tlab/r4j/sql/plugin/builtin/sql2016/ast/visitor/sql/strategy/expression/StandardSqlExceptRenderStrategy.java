package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExceptRenderStrategy;

public class StandardSqlExceptRenderStrategy implements ExceptRenderStrategy {

    @Override
    public String render(ExceptExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "((%s) %s (%s))",
                expression.left().accept(sqlRenderer, ctx),
                expression.distinct() ? "EXCEPT" : "EXCEPT ALL",
                expression.right().accept(sqlRenderer, ctx));
    }
}
