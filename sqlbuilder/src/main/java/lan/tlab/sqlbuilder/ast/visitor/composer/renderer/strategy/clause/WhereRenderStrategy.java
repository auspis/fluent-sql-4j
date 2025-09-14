package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import java.util.Objects;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class WhereRenderStrategy implements ClauseRenderStrategy {

    public String render(Where clause, SqlRenderer sqlRenderer, AstContext ctx) {
        String sql = clause.getCondition().accept(sqlRenderer, ctx);
        if (Objects.isNull(sql) || sql.isBlank()) {
            return "";
        }

        return String.format("WHERE %s", sql);
    }
}
