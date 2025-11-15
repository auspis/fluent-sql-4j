package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.BinaryArithmeticExpressionPsStrategy;

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
