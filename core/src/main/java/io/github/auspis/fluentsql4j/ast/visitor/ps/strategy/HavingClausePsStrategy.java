package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dql.clause.Having;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface HavingClausePsStrategy {
    PreparedStatementSpec handle(Having having, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
