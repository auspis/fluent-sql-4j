package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Substring;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SubstringPsStrategy;

public class StandardSqlSubstringPsStrategy implements SubstringPsStrategy {

    @Override
    public PreparedStatementSpec handle(Substring substring, PreparedStatementRenderer renderer, AstContext ctx) {
        PreparedStatementSpec expressionResult = substring.expression().accept(renderer, ctx);
        PreparedStatementSpec startPositionResult = substring.startPosition().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>(expressionResult.parameters());
        parameters.addAll(startPositionResult.parameters());

        String sql;
        // Check if length is specified (not null)
        if (substring.length() instanceof NullScalarExpression) {
            // SUBSTRING with only expression and start position
            sql = String.format("SUBSTRING(%s, %s)", expressionResult.sql(), startPositionResult.sql());
        } else {
            // SUBSTRING with expression, start position and length
            PreparedStatementSpec lengthResult = substring.length().accept(renderer, ctx);
            parameters.addAll(lengthResult.parameters());
            sql = String.format(
                    "SUBSTRING(%s, %s, %s)", expressionResult.sql(), startPositionResult.sql(), lengthResult.sql());
        }

        return new PreparedStatementSpec(sql, parameters);
    }
}
