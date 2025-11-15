package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lag;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.LagRenderStrategy;

public class StandardSqlLagRenderStrategy implements LagRenderStrategy {
    @Override
    public String render(Lag lag, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("LAG(")
                .append(lag.expression().accept(sqlRenderer, ctx))
                .append(", ")
                .append(lag.offset());

        if (lag.defaultValue() != null) {
            sql.append(", ").append(lag.defaultValue().accept(sqlRenderer, ctx));
        }

        sql.append(")");

        if (lag.overClause() != null) {
            sql.append(" ").append(lag.overClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
