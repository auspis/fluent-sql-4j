package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface NotNullConstraintPsStrategy {
    PreparedStatementSpec handle(
            NotNullConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx);
}
