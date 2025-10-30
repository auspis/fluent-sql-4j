package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.DenseRank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public class DenseRankRenderStrategy implements ExpressionRenderStrategy {

    public String render(DenseRank denseRank, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("DENSE_RANK()");

        if (denseRank.getOverClause() != null) {
            sql.append(" ").append(denseRank.getOverClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
