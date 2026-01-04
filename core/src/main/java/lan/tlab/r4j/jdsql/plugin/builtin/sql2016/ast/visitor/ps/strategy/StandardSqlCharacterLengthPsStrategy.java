package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.string.CharacterLength;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CharacterLengthPsStrategy;

public class StandardSqlCharacterLengthPsStrategy implements CharacterLengthPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CharacterLength characterLength, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var expressionResult = characterLength.expression().accept(astToPsSpecVisitor, ctx);

        String sql = String.format("CHARACTER_LENGTH(%s)", expressionResult.sql());
        return new PreparedStatementSpec(sql, expressionResult.parameters());
    }
}
