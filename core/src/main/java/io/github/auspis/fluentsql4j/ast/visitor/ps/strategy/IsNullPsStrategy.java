package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.predicate.IsNull;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface IsNullPsStrategy {
    PreparedStatementSpec handle(IsNull isNull, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
