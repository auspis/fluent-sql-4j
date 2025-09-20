package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class ReferencesItemRenderStrategy implements SqlItemRenderStrategy {

    public String render(ReferencesItem item, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        return String.format(
                "REFERENCES %s (%s)",
                escapeStrategy.apply(item.getTable()),
                item.getColumns().stream().map(c -> escapeStrategy.apply(c)).collect(Collectors.joining(", ")));
    }
}
