package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.WhenMatchedUpdateRenderStrategy;

public class StandardSqlWhenMatchedUpdateRenderStrategy implements WhenMatchedUpdateRenderStrategy {
    @Override
    public String render(WhenMatchedUpdate action, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("WHEN MATCHED");

        if (action.condition() != null) {
            sql.append(" AND ").append(action.condition().accept(sqlRenderer, ctx));
        }

        sql.append(" THEN UPDATE SET ");

        String updates = action.updateItems().stream()
                .map(item -> item.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));

        sql.append(updates);

        return sql.toString();
    }
}
