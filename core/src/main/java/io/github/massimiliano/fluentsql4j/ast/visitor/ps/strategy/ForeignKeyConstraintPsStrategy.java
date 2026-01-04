package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ForeignKeyConstraintPsStrategy {
    PreparedStatementSpec handle(
            ForeignKeyConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx);
}
