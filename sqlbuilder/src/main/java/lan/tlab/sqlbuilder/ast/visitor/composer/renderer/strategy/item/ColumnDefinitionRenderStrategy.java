package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition.Type;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ColumnDefinitionRenderStrategy implements SqlItemRenderStrategy {

    public String render(ColumnDefinition item, SqlRenderer sqlRenderer) {
        String columnName = sqlRenderer.getEscapeStrategy().apply(item.getName());
        Type type = item.getType();
        String constraints = item.getConstraints()
                .stream()
                .map(c -> c.accept(sqlRenderer))
                .collect(Collectors.joining(", "));
        return String.format("%s %s %s", columnName, type, constraints).trim();
    }
}
