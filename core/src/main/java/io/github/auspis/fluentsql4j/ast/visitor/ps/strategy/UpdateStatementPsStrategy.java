package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.statement.UpdateStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface UpdateStatementPsStrategy {
    PreparedStatementSpec handle(
            UpdateStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
