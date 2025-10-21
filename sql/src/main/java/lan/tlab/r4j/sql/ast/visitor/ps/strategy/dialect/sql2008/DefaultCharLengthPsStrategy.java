package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CharLengthPsStrategy;

public class DefaultCharLengthPsStrategy implements CharLengthPsStrategy {

    @Override
    public PsDto handle(CharLength charLength, PreparedStatementRenderer renderer, AstContext ctx) {
        var expressionResult = charLength.getExpression().accept(renderer, ctx);

        String sql = String.format("CHAR_LENGTH(%s)", expressionResult.sql());
        return new PsDto(sql, expressionResult.parameters());
    }
}
