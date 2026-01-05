package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UnaryArithmeticExpressionPsStrategy;

public class StandardSqlUnaryArithmeticExpressionPsStrategy implements UnaryArithmeticExpressionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            UnaryArithmeticExpression expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec exprResult = expression.expression().accept(renderer, ctx);
        String sql = String.format("(%s%s)", expression.operator(), exprResult.sql());
        return new PreparedStatementSpec(sql, exprResult.parameters());
    }
}
