package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.IndexDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.TableDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.TableDefinitionPsStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StandardSqlTableDefinitionPsStrategy implements TableDefinitionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            TableDefinition tableDefinition, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();

        PreparedStatementSpec tableDto = tableDefinition.table().accept(astToPsSpecVisitor, ctx);
        allParameters.addAll(tableDto.parameters());

        String columnsSql = tableDefinition.columns().stream()
                .map(c -> {
                    PreparedStatementSpec columnDto = astToPsSpecVisitor.visit(c, ctx);
                    allParameters.addAll(columnDto.parameters());
                    return columnDto.sql();
                })
                .collect(Collectors.joining(", "));

        String primaryKeySql = renderPrimaryKey(tableDefinition.primaryKey(), astToPsSpecVisitor, ctx, allParameters);
        String constraintsSql =
                renderConstraints(tableDefinition.constraints(), astToPsSpecVisitor, ctx, allParameters);
        String indexesSql = renderIndexes(tableDefinition.indexes(), astToPsSpecVisitor, ctx, allParameters);

        String sql = tableDto.sql() + " (" + columnsSql + primaryKeySql + constraintsSql + indexesSql + ")";
        return new PreparedStatementSpec(sql, allParameters);
    }

    private String renderPrimaryKey(
            PrimaryKeyDefinition primaryKey,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx,
            List<Object> allParameters) {
        if (primaryKey == null) {
            return "";
        }
        PreparedStatementSpec pkDto = astToPsSpecVisitor.visit(primaryKey, ctx);
        allParameters.addAll(pkDto.parameters());
        return ", " + pkDto.sql();
    }

    private String renderConstraints(
            List<ConstraintDefinition> constraints,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx,
            List<Object> allParameters) {
        if (constraints == null || constraints.isEmpty()) {
            return "";
        }
        String constraintsSql = constraints.stream()
                .map(c -> {
                    PreparedStatementSpec constraintDto = c.accept(astToPsSpecVisitor, ctx);
                    allParameters.addAll(constraintDto.parameters());
                    return constraintDto.sql();
                })
                .collect(Collectors.joining(", "));
        return ", " + constraintsSql;
    }

    private String renderIndexes(
            List<IndexDefinition> indexes,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx,
            List<Object> allParameters) {
        if (indexes == null || indexes.isEmpty()) {
            return "";
        }
        String indexesSql = indexes.stream()
                .map(i -> {
                    PreparedStatementSpec indexDto = astToPsSpecVisitor.visit(i, ctx);
                    allParameters.addAll(indexDto.parameters());
                    return indexDto.sql();
                })
                .collect(Collectors.joining(", "));
        return ", " + indexesSql;
    }
}
