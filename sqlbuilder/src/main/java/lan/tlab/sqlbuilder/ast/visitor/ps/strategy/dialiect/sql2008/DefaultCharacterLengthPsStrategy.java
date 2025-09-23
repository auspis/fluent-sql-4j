package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharacterLength;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.CharacterLengthPsStrategy;

public class DefaultCharacterLengthPsStrategy implements CharacterLengthPsStrategy {

    @Override
    public PsDto handle(CharacterLength characterLength, PreparedStatementVisitor visitor, AstContext ctx) {
        var expressionResult = characterLength.getExpression().accept(visitor, ctx);

        String sql = String.format("CHARACTER_LENGTH(%s)", expressionResult.sql());
        return new PsDto(sql, expressionResult.parameters());
    }
}
