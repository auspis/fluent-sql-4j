package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.escape.EscapeStrategy;

public class ReferencesItemRenderStrategy implements SqlItemRenderStrategy {

    public String render(ReferencesItem item, SqlRenderer sqlRenderer) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        return String.format(
                "REFERENCES %s (%s)",
                escapeStrategy.apply(item.getTable()), 
                item.getColumns()
                        .stream()
                        .map(c -> escapeStrategy.apply(c))
                        .collect(Collectors.joining(", ")));
    }
}
