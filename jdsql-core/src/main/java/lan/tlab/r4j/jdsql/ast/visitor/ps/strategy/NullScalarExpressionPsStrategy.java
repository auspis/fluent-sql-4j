package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface NullScalarExpressionPsStrategy {
    PreparedStatementSpec handle(
            NullScalarExpression nullScalarExpression, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
