package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.common.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.dql.clause.Having;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.HavingRenderStrategy;

public class StandardSqlHavingRenderStrategy implements HavingRenderStrategy {

    @Override
    public String render(Having clause, SqlRenderer sqlRenderer, AstContext ctx) {
        Predicate condition = clause.condition();
        String sql = condition.accept(sqlRenderer, ctx);
        if (sql.isBlank()) {
            return "";
        }
        return "HAVING " + sql;
    }
}
