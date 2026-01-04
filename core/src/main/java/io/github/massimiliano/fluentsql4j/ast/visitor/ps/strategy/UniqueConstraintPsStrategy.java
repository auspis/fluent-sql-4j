package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface UniqueConstraintPsStrategy {
    PreparedStatementSpec handle(
            UniqueConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx);
}
