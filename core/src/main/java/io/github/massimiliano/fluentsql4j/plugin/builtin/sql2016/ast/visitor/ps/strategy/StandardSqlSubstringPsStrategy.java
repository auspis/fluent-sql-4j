package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Substring;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.SubstringPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlSubstringPsStrategy implements SubstringPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Substring substring, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec expressionResult = substring.expression().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec startPositionResult = substring.startPosition().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>(expressionResult.parameters());
        parameters.addAll(startPositionResult.parameters());

        String sql;
        // Check if length is specified (not null)
        if (substring.length() instanceof NullScalarExpression) {
            // SUBSTRING with only expression and start position
            sql = String.format("SUBSTRING(%s, %s)", expressionResult.sql(), startPositionResult.sql());
        } else {
            // SUBSTRING with expression, start position and length
            PreparedStatementSpec lengthResult = substring.length().accept(astToPsSpecVisitor, ctx);
            parameters.addAll(lengthResult.parameters());
            sql = String.format(
                    "SUBSTRING(%s, %s, %s)", expressionResult.sql(), startPositionResult.sql(), lengthResult.sql());
        }

        return new PreparedStatementSpec(sql, parameters);
    }
}
