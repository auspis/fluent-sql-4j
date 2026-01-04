package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface TablePsStrategy {
    PreparedStatementSpec handle(TableIdentifier table, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
