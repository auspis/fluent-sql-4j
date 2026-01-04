package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface BinaryArithmeticExpressionPsStrategy {
    PreparedStatementSpec handle(
            BinaryArithmeticExpression expression, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
