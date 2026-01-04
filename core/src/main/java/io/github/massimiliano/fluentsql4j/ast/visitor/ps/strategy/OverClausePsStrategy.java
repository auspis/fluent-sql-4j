package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.window.OverClause;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface OverClausePsStrategy {
    PreparedStatementSpec handle(OverClause overClause, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
