package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.set.UnionExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface UnionExpressionPsStrategy {
    PreparedStatementSpec handle(
            UnionExpression expression, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
