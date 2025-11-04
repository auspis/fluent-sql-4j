package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedDelete;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.WhenMatchedDeleteRenderStrategy;

public class StandardSqlWhenMatchedDeleteRenderStrategy implements WhenMatchedDeleteRenderStrategy {
    @Override
    public String render(WhenMatchedDelete action, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("WHEN MATCHED");

        if (action.condition() != null) {
            sql.append(" AND ").append(action.condition().accept(sqlRenderer, ctx));
        }

        sql.append(" THEN DELETE");

        return sql.toString();
    }
}
