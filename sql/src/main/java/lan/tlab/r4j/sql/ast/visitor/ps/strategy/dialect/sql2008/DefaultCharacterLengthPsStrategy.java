package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharacterLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CharacterLengthPsStrategy;

public class DefaultCharacterLengthPsStrategy implements CharacterLengthPsStrategy {

    @Override
    public PsDto handle(CharacterLength characterLength, PreparedStatementRenderer renderer, AstContext ctx) {
        var expressionResult = characterLength.getExpression().accept(renderer, ctx);

        String sql = String.format("CHARACTER_LENGTH(%s)", expressionResult.sql());
        return new PsDto(sql, expressionResult.parameters());
    }
}
