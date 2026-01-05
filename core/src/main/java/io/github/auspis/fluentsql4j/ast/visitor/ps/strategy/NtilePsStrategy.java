package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.window.Ntile;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface NtilePsStrategy {
    PreparedStatementSpec handle(Ntile ntile, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
