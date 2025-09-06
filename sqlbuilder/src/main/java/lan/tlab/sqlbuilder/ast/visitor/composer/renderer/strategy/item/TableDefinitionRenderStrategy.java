package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class TableDefinitionRenderStrategy implements SqlItemRenderStrategy {

    public String render(TableDefinition item, SqlRenderer sqlRenderer) {
        // TODO aaa - refactor and test
        StringBuilder builder = new StringBuilder();
        builder.append(item.getTable().accept(sqlRenderer));
        builder.append(" (");

        String columns = item.getColumns().stream().map(sqlRenderer::visit).collect(Collectors.joining(", "));
        builder.append(columns);

        if (item.getPrimaryKey() != null) {
            builder.append(", ");
            builder.append(sqlRenderer.visit(item.getPrimaryKey()));
        }

        builder.append(")");
        return builder.toString();
    }
}
