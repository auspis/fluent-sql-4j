package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Length;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.LengthRenderStrategy;

public class StandardSqlLengthRenderStrategy implements LengthRenderStrategy {

    @Override
    public String render(Length functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("LENGTH(%s)", functionCall.expression().accept(sqlRenderer, ctx));
    }
}
