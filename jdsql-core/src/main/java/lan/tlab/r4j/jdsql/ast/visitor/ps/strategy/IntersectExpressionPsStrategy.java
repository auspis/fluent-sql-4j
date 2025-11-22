package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.set.IntersectExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface IntersectExpressionPsStrategy {

    PsDto handle(IntersectExpression expression, PreparedStatementRenderer renderer, AstContext ctx);
}
