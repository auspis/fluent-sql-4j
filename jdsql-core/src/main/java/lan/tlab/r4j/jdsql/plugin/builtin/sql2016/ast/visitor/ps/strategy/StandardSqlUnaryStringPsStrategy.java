package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnaryStringPsStrategy;

public class StandardSqlUnaryStringPsStrategy implements UnaryStringPsStrategy {

    @Override
    public PsDto handle(UnaryString functionCall, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto expressionDto = functionCall.expression().accept(renderer, ctx);
        String sql = String.format("%s(%s)", functionCall.functionName(), expressionDto.sql());
        return new PsDto(sql, expressionDto.parameters());
    }
}
