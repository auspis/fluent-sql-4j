package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.WhenNotMatchedInsertRenderStrategy;

public class StandardSqlWhenNotMatchedInsertRenderStrategy implements WhenNotMatchedInsertRenderStrategy {
    @Override
    public String render(WhenNotMatchedInsert action, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("WHEN NOT MATCHED");

        if (action.condition() != null) {
            sql.append(" AND ").append(action.condition().accept(sqlRenderer, ctx));
        }

        sql.append(" THEN INSERT");

        if (!action.columns().isEmpty()) {
            String columns = action.columns().stream()
                    .map(col -> col.accept(sqlRenderer, ctx))
                    .collect(Collectors.joining(", "));
            sql.append(" (").append(columns).append(")");
        }

        sql.append(" ").append(action.insertData().accept(sqlRenderer, ctx));

        return sql.toString();
    }
}
