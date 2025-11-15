package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import java.util.Objects;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.WhereRenderStrategy;

public class StandardSqlWhereRenderStrategy implements WhereRenderStrategy {

    @Override
    public String render(Where clause, SqlRenderer sqlRenderer, AstContext ctx) {
        String sql = clause.condition().accept(sqlRenderer, ctx);
        if (Objects.isNull(sql) || sql.isBlank()) {
            return "";
        }

        return String.format("WHERE %s", sql);
    }
}
