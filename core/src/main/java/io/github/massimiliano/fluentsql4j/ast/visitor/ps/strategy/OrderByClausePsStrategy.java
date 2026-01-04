package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface OrderByClausePsStrategy {
    PreparedStatementSpec handle(OrderBy orderBy, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
