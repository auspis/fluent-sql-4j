package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface ScalarSubqueryPsStrategy {
    PsDto handle(ScalarSubquery subquery, PreparedStatementRenderer renderer, AstContext ctx);
}
