package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.UnaryArithmeticExpressionPsStrategy;

public class DefaultUnaryArithmeticExpressionPsStrategy implements UnaryArithmeticExpressionPsStrategy {
    @Override
    public PsDto handle(UnaryArithmeticExpression expression, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto exprResult = expression.getExpression().accept(visitor, ctx);
        String sql = String.format("(%s%s)", expression.getOperator(), exprResult.sql());
        return new PsDto(sql, exprResult.parameters());
    }
}
