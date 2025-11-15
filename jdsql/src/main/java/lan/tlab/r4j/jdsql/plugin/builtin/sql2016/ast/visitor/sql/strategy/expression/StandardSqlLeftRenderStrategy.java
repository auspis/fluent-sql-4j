package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Left;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.LeftRenderStrategy;

public class StandardSqlLeftRenderStrategy implements LeftRenderStrategy {

    @Override
    public String render(Left functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "LEFT(%s, %s)",
                functionCall.expression().accept(sqlRenderer, ctx),
                functionCall.length().accept(sqlRenderer, ctx));
    }
}
