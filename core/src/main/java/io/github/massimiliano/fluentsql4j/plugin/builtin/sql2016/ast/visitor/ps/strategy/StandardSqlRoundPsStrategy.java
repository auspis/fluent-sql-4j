package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.number.Round;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.RoundPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlRoundPsStrategy implements RoundPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Round round, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec numericResult = round.numericExpression().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>(numericResult.parameters());
        String sql;

        // Check if decimalPlaces is specified (not null)
        if (round.decimalPlaces() instanceof NullScalarExpression) {
            // ROUND with only one parameter
            sql = String.format("ROUND(%s)", numericResult.sql());
        } else {
            // ROUND with two parameters
            PreparedStatementSpec decimalPlacesResult = round.decimalPlaces().accept(astToPsSpecVisitor, ctx);
            parameters.addAll(decimalPlacesResult.parameters());
            sql = String.format("ROUND(%s, %s)", numericResult.sql(), decimalPlacesResult.sql());
        }

        return new PreparedStatementSpec(sql, parameters);
    }
}
