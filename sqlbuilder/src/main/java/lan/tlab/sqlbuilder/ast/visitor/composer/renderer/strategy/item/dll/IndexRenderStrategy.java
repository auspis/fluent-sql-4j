package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Index;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.escape.EscapeStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class IndexRenderStrategy implements SqlItemRenderStrategy {

    public String render(Index index, SqlRenderer sqlRenderer) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        return String.format(
                "INDEX %s (%s)",
                escapeStrategy.apply(index.getName()),
                index.getColumnNames().stream()
                        .map(escapeStrategy::apply)
                        .collect(Collectors.joining(", ")));
    }
}
