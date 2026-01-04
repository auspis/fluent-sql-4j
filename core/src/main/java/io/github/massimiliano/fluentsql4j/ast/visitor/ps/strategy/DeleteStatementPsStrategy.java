package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dml.statement.DeleteStatement;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface DeleteStatementPsStrategy {
    PreparedStatementSpec handle(
            DeleteStatement deleteStatement, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
