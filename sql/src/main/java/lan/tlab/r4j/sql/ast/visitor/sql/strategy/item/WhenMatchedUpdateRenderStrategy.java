package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class WhenMatchedUpdateRenderStrategy {

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
