package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Index;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class TableDefinitionRenderStrategy implements SqlItemRenderStrategy {

    public String render(TableDefinition item, SqlRenderer sqlRenderer) {
        StringBuilder builder = new StringBuilder();
        builder.append(item.getTable().accept(sqlRenderer));
        builder.append(" (");
        builder.append(item.getColumns().stream().map(sqlRenderer::visit).collect(Collectors.joining(", ")));
        builder.append(primaryKey(item.getPrimaryKey(), sqlRenderer));
        builder.append(indexes(item.getIndexes(), sqlRenderer));
        builder.append(")");
        return builder.toString();
    }

    private String indexes(List<Index> indexes, SqlRenderer sqlRenderer) {
        if (indexes == null || indexes.isEmpty()) {
            return "";
        }
        return ", " + indexes.stream().map(sqlRenderer::visit).collect(Collectors.joining(", "));
    }

    private String primaryKey(PrimaryKey primaryKey, SqlRenderer sqlRenderer) {
        if (primaryKey == null) {
            return "";
        }
        return ", " + sqlRenderer.visit(primaryKey);
    }
}
