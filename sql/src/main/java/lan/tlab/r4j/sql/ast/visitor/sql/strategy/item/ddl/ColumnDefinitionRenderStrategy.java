package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class ColumnDefinitionRenderStrategy implements SqlItemRenderStrategy {

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
