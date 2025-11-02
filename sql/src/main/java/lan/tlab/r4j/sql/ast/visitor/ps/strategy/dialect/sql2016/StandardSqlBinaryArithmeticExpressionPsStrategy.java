package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.BinaryArithmeticExpressionPsStrategy;

public class StandardSqlBinaryArithmeticExpressionPsStrategy implements BinaryArithmeticExpressionPsStrategy {
    @Override
    public PsDto handle(BinaryArithmeticExpression expression, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto lhsResult = expression.lhs().accept(renderer, ctx);
        PsDto rhsResult = expression.rhs().accept(renderer, ctx);

        String sql = String.format("(%s %s %s)", lhsResult.sql(), expression.operator(), rhsResult.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(lhsResult.parameters());
        parameters.addAll(rhsResult.parameters());

        return new PsDto(sql, parameters);
    }
}
