package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.IndexDefinitionRenderStrategy;

public class StandardSqlIndexDefinitionRenderStrategy implements IndexDefinitionRenderStrategy {
    @Override
    public String render(IndexDefinition indexDefinition, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        return String.format(
                "INDEX %s (%s)",
                escapeStrategy.apply(indexDefinition.name()),
                indexDefinition.columnNames().stream()
                        .map(escapeStrategy::apply)
                        .collect(Collectors.joining(", ")));
    }
}
