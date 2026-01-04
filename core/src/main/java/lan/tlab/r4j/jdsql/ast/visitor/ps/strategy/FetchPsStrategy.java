package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface FetchPsStrategy {
    PreparedStatementSpec handle(
            Fetch pagination, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
