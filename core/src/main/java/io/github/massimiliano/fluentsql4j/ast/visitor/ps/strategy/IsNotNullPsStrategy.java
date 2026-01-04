package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.predicate.IsNotNull;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface IsNotNullPsStrategy {
    PreparedStatementSpec handle(IsNotNull isNotNull, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
