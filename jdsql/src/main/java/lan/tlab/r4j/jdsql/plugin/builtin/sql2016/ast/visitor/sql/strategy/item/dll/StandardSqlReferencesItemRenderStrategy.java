package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.ReferencesItemRenderStrategy;

public class StandardSqlReferencesItemRenderStrategy implements ReferencesItemRenderStrategy {
    @Override
    public String render(ReferencesItem item, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        return String.format(
                "REFERENCES %s (%s)",
                escapeStrategy.apply(item.table()),
                item.columns().stream().map(c -> escapeStrategy.apply(c)).collect(Collectors.joining(", ")));
    }
}
