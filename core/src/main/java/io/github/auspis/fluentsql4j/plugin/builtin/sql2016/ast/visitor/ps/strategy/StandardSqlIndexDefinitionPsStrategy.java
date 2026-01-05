package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.ddl.definition.IndexDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;

public class StandardSqlIndexDefinitionPsStrategy implements IndexDefinitionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            IndexDefinition index, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        // Index definitions are static DDL elements without parameters
        // Inline rendering logic from StandardSqlIndexDefinitionRenderStrategy
        String sql = String.format(
                "INDEX %s (%s)",
                astToPsSpecVisitor.getEscapeStrategy().apply(index.name()),
                index.columnNames().stream()
                        .map(astToPsSpecVisitor.getEscapeStrategy()::apply)
                        .collect(java.util.stream.Collectors.joining(", ")));
        return new PreparedStatementSpec(sql, List.of());
    }
}
