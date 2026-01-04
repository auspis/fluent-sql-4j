package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.UnionExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface UnionExpressionPsStrategy {
    PreparedStatementSpec handle(
            UnionExpression expression, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
