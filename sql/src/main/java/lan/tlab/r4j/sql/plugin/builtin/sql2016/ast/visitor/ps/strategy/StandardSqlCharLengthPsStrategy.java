package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.CharLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CharLengthPsStrategy;

public class StandardSqlCharLengthPsStrategy implements CharLengthPsStrategy {

    @Override
    public PsDto handle(CharLength charLength, PreparedStatementRenderer renderer, AstContext ctx) {
        var expressionResult = charLength.expression().accept(renderer, ctx);

        String sql = String.format("CHAR_LENGTH(%s)", expressionResult.sql());
        return new PsDto(sql, expressionResult.parameters());
    }
}
