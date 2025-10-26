package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnaryArithmeticExpressionPsStrategy;

public class DefaultUnaryArithmeticExpressionPsStrategy implements UnaryArithmeticExpressionPsStrategy {
    @Override
    public PsDto handle(UnaryArithmeticExpression expression, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto exprResult = expression.expression().accept(renderer, ctx);
        String sql = String.format("(%s%s)", expression.operator(), exprResult.sql());
        return new PsDto(sql, exprResult.parameters());
    }
}
