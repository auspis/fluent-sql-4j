package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.predicate.In;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface InPsStrategy {
    PreparedStatementSpec handle(In in, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
