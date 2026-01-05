package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface NullScalarExpressionPsStrategy {
    PreparedStatementSpec handle(
            NullScalarExpression nullScalarExpression, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
