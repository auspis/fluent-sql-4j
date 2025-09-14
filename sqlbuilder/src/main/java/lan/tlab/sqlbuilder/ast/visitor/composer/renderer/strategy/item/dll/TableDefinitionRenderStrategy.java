package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Index;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

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

    private String constraints(List<Constraint> constraints, SqlRenderer sqlRenderer, AstContext ctx) {
        if (constraints == null || constraints.isEmpty()) {
            return "";
        }
        return ", " + constraints.stream().map(c -> c.accept(sqlRenderer, ctx)).collect(Collectors.joining(", "));
    }

    private String indexes(List<Index> indexes, SqlRenderer sqlRenderer, AstContext ctx) {
        if (indexes == null || indexes.isEmpty()) {
            return "";
        }
        return ", " + indexes.stream().map(i -> sqlRenderer.visit(i, ctx)).collect(Collectors.joining(", "));
    }

    private String primaryKey(PrimaryKey primaryKey, SqlRenderer sqlRenderer, AstContext ctx) {
        if (primaryKey == null) {
            return "";
        }
        return ", " + sqlRenderer.visit(primaryKey, ctx);
    }
}
