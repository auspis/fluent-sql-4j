package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;

public class StandardSqlForeignKeyConstraintPsStrategy implements ForeignKeyConstraintPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ForeignKeyConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx) {
        // Foreign key constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlForeignKeyConstraintRenderStrategy
        String columns = constraint.columns().stream()
                .map(c -> astToPsSpecVisitor.getEscapeStrategy().apply(c))
                .collect(java.util.stream.Collectors.joining(", "));
        PreparedStatementSpec referencesDto = constraint.references().accept(astToPsSpecVisitor, ctx);
        String sql = String.format("FOREIGN KEY (%s) %s", columns, referencesDto.sql());
        return new PreparedStatementSpec(sql, referencesDto.parameters());
    }
}
