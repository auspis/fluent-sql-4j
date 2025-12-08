package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnaryArithmeticExpressionPsStrategy;

public class StandardSqlUnaryArithmeticExpressionPsStrategy implements UnaryArithmeticExpressionPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            UnaryArithmeticExpression expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec exprResult = expression.expression().accept(renderer, ctx);
        String sql = String.format("(%s%s)", expression.operator(), exprResult.sql());
        return new PreparedStatementSpec(sql, exprResult.parameters());
    }
}
