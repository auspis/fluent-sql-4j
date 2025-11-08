package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnaryArithmeticExpressionPsStrategy;

public class StandardSqlUnaryArithmeticExpressionPsStrategy implements UnaryArithmeticExpressionPsStrategy {
    @Override
    public PsDto handle(UnaryArithmeticExpression expression, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto exprResult = expression.expression().accept(renderer, ctx);
        String sql = String.format("(%s%s)", expression.operator(), exprResult.sql());
        return new PsDto(sql, exprResult.parameters());
    }
}
