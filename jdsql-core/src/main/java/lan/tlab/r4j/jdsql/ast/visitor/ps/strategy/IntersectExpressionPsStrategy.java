package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.set.IntersectExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface IntersectExpressionPsStrategy {

    PreparedStatementSpec handle(IntersectExpression expression, PreparedStatementRenderer renderer, AstContext ctx);
}
