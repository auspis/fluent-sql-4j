package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;
import java.util.List;

public class StandardSqlUniqueConstraintPsStrategy implements UniqueConstraintPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            UniqueConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx) {
        // Unique constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlUniqueConstraintRenderStrategy
        String columns = constraint.columns().stream()
                .map(c -> astToPsSpecVisitor.getEscapeStrategy().apply(c))
                .collect(java.util.stream.Collectors.joining(", "));
        String sql = String.format("UNIQUE (%s)", columns);
        return new PreparedStatementSpec(sql, List.of());
    }
}
