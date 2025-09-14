package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class ColumnDefinitionRenderStrategy implements SqlItemRenderStrategy {

    public String render(ColumnDefinition item, SqlRenderer sqlRenderer, AstContext ctx) {
        if (item.equals(ColumnDefinition.nullObject())) {
            return "";
        }

        String columnName = sqlRenderer.getEscapeStrategy().apply(item.getName());
        String type = item.getType().accept(sqlRenderer, ctx);
        String constraints = Stream.of(item.getNotNullConstraint(), item.getDefaultConstraint())
                .filter(c -> c != null)
                .map(c -> c.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(" "))
                .trim();
        return String.format("%s %s %s", columnName, type, constraints).trim();
    }
}
