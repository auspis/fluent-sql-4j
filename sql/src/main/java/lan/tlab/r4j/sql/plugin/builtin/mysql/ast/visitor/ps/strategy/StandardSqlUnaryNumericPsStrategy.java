package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.number.UnaryNumeric;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnaryNumericPsStrategy;

public class StandardSqlUnaryNumericPsStrategy implements UnaryNumericPsStrategy {

    @Override
    public PsDto handle(UnaryNumeric functionCall, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto expressionDto = functionCall.numericExpression().accept(renderer, ctx);
        String sql = String.format("%s(%s)", functionCall.functionName(), expressionDto.sql());
        return new PsDto(sql, expressionDto.parameters());
    }
}
