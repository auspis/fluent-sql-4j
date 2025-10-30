package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Lag;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public class LagRenderStrategy implements ExpressionRenderStrategy {

    public String render(Lag lag, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("LAG(")
                .append(lag.getExpression().accept(sqlRenderer, ctx))
                .append(", ")
                .append(lag.getOffset());

        if (lag.getDefaultValue() != null) {
            sql.append(", ").append(lag.getDefaultValue().accept(sqlRenderer, ctx));
        }

        sql.append(")");

        if (lag.getOverClause() != null) {
            sql.append(" ").append(lag.getOverClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
