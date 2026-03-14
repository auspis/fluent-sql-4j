package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.statement.TruncateStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface TruncateStatementPsStrategy {
    PreparedStatementSpec handle(
            TruncateStatement truncateStatement, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
