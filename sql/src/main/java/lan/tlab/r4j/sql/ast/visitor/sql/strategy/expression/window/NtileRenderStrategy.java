package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Ntile;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public class NtileRenderStrategy implements ExpressionRenderStrategy {

    public String render(Ntile ntile, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("NTILE(").append(ntile.buckets()).append(")");

        if (ntile.overClause() != null) {
            sql.append(" ").append(ntile.overClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
