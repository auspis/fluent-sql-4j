package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.common.expression.scalar.window.DenseRank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window.DenseRankRenderStrategy;

public class StandardSqlDenseRankRenderStrategy implements DenseRankRenderStrategy {
    @Override
    public String render(DenseRank denseRank, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("DENSE_RANK()");

        if (denseRank.overClause() != null) {
            sql.append(" ").append(denseRank.overClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
