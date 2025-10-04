package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface ScalarSubqueryPsStrategy {
    PsDto handle(ScalarSubquery subquery, PreparedStatementVisitor visitor, AstContext ctx);
}
