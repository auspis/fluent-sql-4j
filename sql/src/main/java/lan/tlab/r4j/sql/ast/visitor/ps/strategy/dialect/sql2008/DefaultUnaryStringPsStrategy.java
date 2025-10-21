package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.UnaryString;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnaryStringPsStrategy;

public class DefaultUnaryStringPsStrategy implements UnaryStringPsStrategy {

    @Override
    public PsDto handle(UnaryString functionCall, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto expressionDto = functionCall.getExpression().accept(renderer, ctx);
        String sql = String.format("%s(%s)", functionCall.getFunctionName(), expressionDto.sql());
        return new PsDto(sql, expressionDto.parameters());
    }
}
