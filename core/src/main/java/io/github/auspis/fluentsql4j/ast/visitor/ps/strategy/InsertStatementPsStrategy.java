package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.statement.InsertStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface InsertStatementPsStrategy {
    PreparedStatementSpec handle(
            InsertStatement insertStatement, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
