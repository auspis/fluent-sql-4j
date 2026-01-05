package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.BinaryArithmeticExpressionPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlBinaryArithmeticExpressionPsStrategy implements BinaryArithmeticExpressionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            BinaryArithmeticExpression expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec lhsResult = expression.lhs().accept(renderer, ctx);
        PreparedStatementSpec rhsResult = expression.rhs().accept(renderer, ctx);

        String sql = String.format("(%s %s %s)", lhsResult.sql(), expression.operator(), rhsResult.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(lhsResult.parameters());
        parameters.addAll(rhsResult.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
