package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Cast;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CastRenderStrategy;

public class StandardSqlCastRenderStrategy implements CastRenderStrategy {

    @Override
    public String render(Cast functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "CAST(%s AS %s)", functionCall.expression().accept(sqlRenderer, ctx), functionCall.dataType());
    }
}
