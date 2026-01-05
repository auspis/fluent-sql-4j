package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface PrimaryKeyPsStrategy {
    PreparedStatementSpec handle(
            PrimaryKeyDefinition item, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
