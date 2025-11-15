package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Mod;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ModRenderStrategy;

public class StandardSqlModRenderStrategy implements ModRenderStrategy {

    @Override
    public String render(Mod functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "MOD(%s, %s)",
                functionCall.dividend().accept(sqlRenderer, ctx),
                functionCall.divisor().accept(sqlRenderer, ctx));
    }
}
