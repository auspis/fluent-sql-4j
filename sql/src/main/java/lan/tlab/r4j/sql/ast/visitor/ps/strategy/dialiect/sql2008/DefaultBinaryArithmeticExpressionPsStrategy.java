package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.BinaryArithmeticExpressionPsStrategy;

public class DefaultBinaryArithmeticExpressionPsStrategy implements BinaryArithmeticExpressionPsStrategy {
    @Override
    public PsDto handle(BinaryArithmeticExpression expression, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto lhsResult = expression.getLhs().accept(visitor, ctx);
        PsDto rhsResult = expression.getRhs().accept(visitor, ctx);

        String sql = String.format("(%s %s %s)", lhsResult.sql(), expression.getOperator(), rhsResult.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(lhsResult.parameters());
        parameters.addAll(rhsResult.parameters());

        return new PsDto(sql, parameters);
    }
}
