package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.ColumnDefinitionRenderStrategy;

public class StandardSqlColumnDefinitionRenderStrategy implements ColumnDefinitionRenderStrategy {
    @Override
    public String render(ColumnDefinition item, SqlRenderer sqlRenderer, AstContext ctx) {
        if (item.equals(ColumnDefinition.nullObject())) {
            return "";
        }

        String columnName = sqlRenderer.getEscapeStrategy().apply(item.name());
        String type = item.type().accept(sqlRenderer, ctx);
        String constraints = Stream.of(item.notNullConstraint(), item.defaultConstraint())
                .filter(c -> c != null)
                .map(c -> c.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(" "))
                .trim();
        return String.format("%s %s %s", columnName, type, constraints).trim();
    }
}
