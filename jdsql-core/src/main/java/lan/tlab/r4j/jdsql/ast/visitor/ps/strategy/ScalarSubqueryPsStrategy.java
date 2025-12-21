package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface ScalarSubqueryPsStrategy {
    PreparedStatementSpec handle(
            ScalarSubquery subquery, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
