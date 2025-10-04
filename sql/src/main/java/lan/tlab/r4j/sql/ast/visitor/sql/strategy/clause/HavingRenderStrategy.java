package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.conditional.having.Having;
import lan.tlab.r4j.sql.ast.expression.bool.BooleanExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class HavingRenderStrategy implements ClauseRenderStrategy {

    public String render(Having clause, SqlRenderer sqlRenderer, AstContext ctx) {
        BooleanExpression condition = clause.getCondition();
        String sql = condition.accept(sqlRenderer, ctx);
        if (sql.isBlank()) {
            return "";
        }
        return "HAVING " + sql;
    }
}
