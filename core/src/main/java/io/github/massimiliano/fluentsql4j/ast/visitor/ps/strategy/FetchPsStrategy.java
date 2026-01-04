package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.clause.Fetch;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface FetchPsStrategy {
    PreparedStatementSpec handle(
            Fetch pagination, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
