package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.predicate.AndOr;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface AndOrPsStrategy {
    PreparedStatementSpec handle(AndOr andOr, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
