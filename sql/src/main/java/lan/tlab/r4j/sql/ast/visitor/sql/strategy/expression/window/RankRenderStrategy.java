package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Rank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public class RankRenderStrategy implements ExpressionRenderStrategy {

    public String render(Rank rank, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("RANK()");

        if (rank.getOverClause() != null) {
            sql.append(" ").append(rank.getOverClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
