package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnaryNumericPsStrategy;

public class DefaultUnaryNumericPsStrategy implements UnaryNumericPsStrategy {

    @Override
    public PsDto handle(UnaryNumeric functionCall, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto expressionDto = functionCall.getNumericExpression().accept(visitor, ctx);
        String sql = String.format("%s(%s)", functionCall.getFunctionName(), expressionDto.sql());
        return new PsDto(sql, expressionDto.parameters());
    }
}
