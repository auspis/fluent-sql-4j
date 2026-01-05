package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;

public class StandardSqlPrimaryKeyPsStrategy implements PrimaryKeyPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            PrimaryKeyDefinition item, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        // Primary key constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlPrimaryKeyRenderStrategy
        String columns = item.columns().stream()
                .map(c -> astToPsSpecVisitor.getEscapeStrategy().apply(c))
                .collect(java.util.stream.Collectors.joining(", "));
        String sql = String.format("PRIMARY KEY (%s)", columns);
        return new PreparedStatementSpec(sql, List.of());
    }
}
