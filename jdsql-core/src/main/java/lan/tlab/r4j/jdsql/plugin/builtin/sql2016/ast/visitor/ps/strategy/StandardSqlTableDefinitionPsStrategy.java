package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.IndexDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.TableDefinitionPsStrategy;

public class StandardSqlTableDefinitionPsStrategy implements TableDefinitionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            TableDefinition tableDefinition, PreparedStatementRenderer renderer, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();

        PreparedStatementSpec tableDto = tableDefinition.table().accept(renderer, ctx);
        allParameters.addAll(tableDto.parameters());

        String columnsSql = tableDefinition.columns().stream()
                .map(c -> {
                    PreparedStatementSpec columnDto = renderer.visit(c, ctx);
                    allParameters.addAll(columnDto.parameters());
                    return columnDto.sql();
                })
                .collect(Collectors.joining(", "));

        String primaryKeySql = renderPrimaryKey(tableDefinition.primaryKey(), renderer, ctx, allParameters);
        String constraintsSql = renderConstraints(tableDefinition.constraints(), renderer, ctx, allParameters);
        String indexesSql = renderIndexes(tableDefinition.indexes(), renderer, ctx, allParameters);

        String sql = tableDto.sql() + " (" + columnsSql + primaryKeySql + constraintsSql + indexesSql + ")";
        return new PreparedStatementSpec(sql, allParameters);
    }

    private String renderPrimaryKey(
            PrimaryKeyDefinition primaryKey,
            PreparedStatementRenderer renderer,
            AstContext ctx,
            List<Object> allParameters) {
        if (primaryKey == null) {
            return "";
        }
        PreparedStatementSpec pkDto = renderer.visit(primaryKey, ctx);
        allParameters.addAll(pkDto.parameters());
        return ", " + pkDto.sql();
    }

    private String renderConstraints(
            List<ConstraintDefinition> constraints,
            PreparedStatementRenderer renderer,
            AstContext ctx,
            List<Object> allParameters) {
        if (constraints == null || constraints.isEmpty()) {
            return "";
        }
        String constraintsSql = constraints.stream()
                .map(c -> {
                    PreparedStatementSpec constraintDto = c.accept(renderer, ctx);
                    allParameters.addAll(constraintDto.parameters());
                    return constraintDto.sql();
                })
                .collect(Collectors.joining(", "));
        return ", " + constraintsSql;
    }

    private String renderIndexes(
            List<IndexDefinition> indexes,
            PreparedStatementRenderer renderer,
            AstContext ctx,
            List<Object> allParameters) {
        if (indexes == null || indexes.isEmpty()) {
            return "";
        }
        String indexesSql = indexes.stream()
                .map(i -> {
                    PreparedStatementSpec indexDto = renderer.visit(i, ctx);
                    allParameters.addAll(indexDto.parameters());
                    return indexDto.sql();
                })
                .collect(Collectors.joining(", "));
        return ", " + indexesSql;
    }
}
