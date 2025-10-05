package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll;

import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class TableDefinitionRenderStrategy implements SqlItemRenderStrategy {

    public String render(TableDefinition item, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder builder = new StringBuilder();
        builder.append(item.getTable().accept(sqlRenderer, ctx));
        builder.append(" (");
        builder.append(
                item.getColumns().stream().map(c -> sqlRenderer.visit(c, ctx)).collect(Collectors.joining(", ")));
        builder.append(primaryKey(item.getPrimaryKey(), sqlRenderer, ctx));
        builder.append(constraints(item.getConstraints(), sqlRenderer, ctx));
        builder.append(indexes(item.getIndexes(), sqlRenderer, ctx));
        builder.append(")");
        return builder.toString();
    }

    private String constraints(List<ConstraintDefinition> constraints, SqlRenderer sqlRenderer, AstContext ctx) {
        if (constraints == null || constraints.isEmpty()) {
            return "";
        }
        return ", " + constraints.stream().map(c -> c.accept(sqlRenderer, ctx)).collect(Collectors.joining(", "));
    }

    private String indexes(List<IndexDefinition> indexes, SqlRenderer sqlRenderer, AstContext ctx) {
        if (indexes == null || indexes.isEmpty()) {
            return "";
        }
        return ", " + indexes.stream().map(i -> sqlRenderer.visit(i, ctx)).collect(Collectors.joining(", "));
    }

    private String primaryKey(PrimaryKeyDefinition primaryKey, SqlRenderer sqlRenderer, AstContext ctx) {
        if (primaryKey == null) {
            return "";
        }
        return ", " + sqlRenderer.visit(primaryKey, ctx);
    }
}
