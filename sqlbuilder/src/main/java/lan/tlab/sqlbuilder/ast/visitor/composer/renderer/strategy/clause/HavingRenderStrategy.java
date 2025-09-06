package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class HavingRenderStrategy implements ClauseRenderStrategy {

    public String render(Having clause, SqlRenderer sqlRenderer) {
        BooleanExpression condition = clause.getCondition();
        String sql = condition.accept(sqlRenderer);
        if (sql.isBlank()) {
            return "";
        }
        return "HAVING " + sql;
    }
}
