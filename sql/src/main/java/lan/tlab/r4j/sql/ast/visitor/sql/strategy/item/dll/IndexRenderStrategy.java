package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.item.ddl.Index;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class IndexRenderStrategy implements SqlItemRenderStrategy {

    public String render(Index index, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        return String.format(
                "INDEX %s (%s)",
                escapeStrategy.apply(index.getName()),
                index.getColumnNames().stream().map(escapeStrategy::apply).collect(Collectors.joining(", ")));
    }
}
