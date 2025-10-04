package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import java.util.Objects;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class WhereRenderStrategy implements ClauseRenderStrategy {

    public String render(Where clause, SqlRenderer sqlRenderer, AstContext ctx) {
        String sql = clause.getCondition().accept(sqlRenderer, ctx);
        if (Objects.isNull(sql) || sql.isBlank()) {
            return "";
        }

        return String.format("WHERE %s", sql);
    }
}
