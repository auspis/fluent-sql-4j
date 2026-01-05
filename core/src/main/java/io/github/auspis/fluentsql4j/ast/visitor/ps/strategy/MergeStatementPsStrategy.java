package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface MergeStatementPsStrategy {
    PreparedStatementSpec handle(
            MergeStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
