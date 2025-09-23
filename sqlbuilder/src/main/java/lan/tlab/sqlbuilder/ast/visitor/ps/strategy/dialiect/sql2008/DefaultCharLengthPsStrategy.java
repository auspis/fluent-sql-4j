package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.CharLengthPsStrategy;

public class DefaultCharLengthPsStrategy implements CharLengthPsStrategy {

    @Override
    public PsDto handle(CharLength charLength, PreparedStatementVisitor visitor, AstContext ctx) {
        var expressionResult = charLength.getExpression().accept(visitor, ctx);

        String sql = String.format("CHAR_LENGTH(%s)", expressionResult.sql());
        return new PsDto(sql, expressionResult.parameters());
    }
}
