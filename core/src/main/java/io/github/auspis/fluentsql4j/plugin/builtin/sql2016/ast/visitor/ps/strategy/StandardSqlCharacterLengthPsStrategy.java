package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.string.CharacterLength;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CharacterLengthPsStrategy;

public class StandardSqlCharacterLengthPsStrategy implements CharacterLengthPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CharacterLength characterLength, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var expressionResult = characterLength.expression().accept(astToPsSpecVisitor, ctx);

        String sql = String.format("CHARACTER_LENGTH(%s)", expressionResult.sql());
        return new PreparedStatementSpec(sql, expressionResult.parameters());
    }
}
