package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.set.IntersectExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface IntersectExpressionPsStrategy {

    PreparedStatementSpec handle(
            IntersectExpression expression, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
