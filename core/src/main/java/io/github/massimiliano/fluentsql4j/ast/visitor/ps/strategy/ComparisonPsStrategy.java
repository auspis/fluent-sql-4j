package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.predicate.Comparison;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ComparisonPsStrategy {
    PreparedStatementSpec handle(Comparison comparison, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
