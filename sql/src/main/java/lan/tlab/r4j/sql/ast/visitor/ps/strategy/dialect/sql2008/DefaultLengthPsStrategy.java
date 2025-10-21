package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Length;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LengthPsStrategy;

public class DefaultLengthPsStrategy implements LengthPsStrategy {

    @Override
    public PsDto handle(Length length, PreparedStatementRenderer renderer, AstContext ctx) {
        var expressionResult = length.getExpression().accept(renderer, ctx);

        String sql = String.format("LENGTH(%s)", expressionResult.sql());
        return new PsDto(sql, expressionResult.parameters());
    }
}
