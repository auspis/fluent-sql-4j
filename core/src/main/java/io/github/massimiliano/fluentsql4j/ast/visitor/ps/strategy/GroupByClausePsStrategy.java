package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.clause.GroupBy;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface GroupByClausePsStrategy {
    PreparedStatementSpec handle(GroupBy groupBy, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
