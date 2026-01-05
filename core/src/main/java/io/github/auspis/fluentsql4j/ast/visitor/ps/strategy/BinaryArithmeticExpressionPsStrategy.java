package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface BinaryArithmeticExpressionPsStrategy {
    PreparedStatementSpec handle(
            BinaryArithmeticExpression expression, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
