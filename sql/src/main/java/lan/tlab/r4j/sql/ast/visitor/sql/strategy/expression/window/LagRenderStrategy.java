package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Lag;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public class LagRenderStrategy implements ExpressionRenderStrategy {

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
