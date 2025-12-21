package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.statement.MergeStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface MergeStatementPsStrategy {
    PreparedStatementSpec handle(
            MergeStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
