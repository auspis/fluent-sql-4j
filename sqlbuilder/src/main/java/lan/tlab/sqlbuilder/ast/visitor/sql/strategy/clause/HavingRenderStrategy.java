package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

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
