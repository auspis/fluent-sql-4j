package lan.tlab.r4j.jdsql.plugin.builtin.oracle.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.set.ExceptExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExceptRenderStrategy;

public class OracleExceptRenderStrategy implements ExceptRenderStrategy {

    @Override
    public String render(ExceptExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "((%s) MINUS (%s))",
                expression.left().accept(sqlRenderer, ctx), expression.right().accept(sqlRenderer, ctx));
    }
}
