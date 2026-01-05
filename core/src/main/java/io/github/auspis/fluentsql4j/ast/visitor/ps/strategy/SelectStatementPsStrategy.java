package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface SelectStatementPsStrategy {
    PreparedStatementSpec handle(
            SelectStatement selectStatement, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
