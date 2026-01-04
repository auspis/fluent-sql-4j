package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.source.FromSubquery;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface FromSubqueryPsStrategy {
    PreparedStatementSpec handle(FromSubquery fromSubquery, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
