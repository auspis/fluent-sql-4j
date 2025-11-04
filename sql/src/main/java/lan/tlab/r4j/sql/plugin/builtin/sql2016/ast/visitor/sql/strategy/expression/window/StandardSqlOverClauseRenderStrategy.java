package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.OverClause;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window.OverClauseRenderStrategy;

public class StandardSqlOverClauseRenderStrategy implements OverClauseRenderStrategy {
    @Override
    public String render(OverClause overClause, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("OVER (");

        if (overClause.partitionBy() != null && !overClause.partitionBy().isEmpty()) {
            sql.append("PARTITION BY ");
            sql.append(overClause.partitionBy().stream()
                    .map(expr -> expr.accept(sqlRenderer, ctx))
                    .collect(Collectors.joining(", ")));
        }

        if (overClause.orderBy() != null && !overClause.orderBy().isEmpty()) {
            if (overClause.partitionBy() != null && !overClause.partitionBy().isEmpty()) {
                sql.append(" ");
            }

            sql.append("ORDER BY ");
            sql.append(overClause.orderBy().stream()
                    .map(sort -> sort.accept(sqlRenderer, ctx))
                    .collect(Collectors.joining(", ")));
        }

        sql.append(")");
        return sql.toString();
    }
}
