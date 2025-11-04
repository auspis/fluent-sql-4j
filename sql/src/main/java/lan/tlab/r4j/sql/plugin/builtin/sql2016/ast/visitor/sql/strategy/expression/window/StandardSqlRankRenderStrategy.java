package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Rank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window.RankRenderStrategy;

public class StandardSqlRankRenderStrategy implements RankRenderStrategy {
    @Override
    public String render(Rank rank, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("RANK()");

        if (rank.overClause() != null) {
            sql.append(" ").append(rank.overClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
