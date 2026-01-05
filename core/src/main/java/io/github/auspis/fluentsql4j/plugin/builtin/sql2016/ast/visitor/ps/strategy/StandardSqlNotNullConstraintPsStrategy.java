package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;
import java.util.List;

public class StandardSqlNotNullConstraintPsStrategy implements NotNullConstraintPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            NotNullConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx) {
        // NOT NULL constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlNotNullConstraintRenderStrategy
        return new PreparedStatementSpec("NOT NULL", List.of());
    }
}
