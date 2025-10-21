package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SubstringPsStrategy;

public class DefaultSubstringPsStrategy implements SubstringPsStrategy {

    @Override
    public PsDto handle(Substring substring, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto expressionResult = substring.getExpression().accept(renderer, ctx);
        PsDto startPositionResult = substring.getStartPosition().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>(expressionResult.parameters());
        parameters.addAll(startPositionResult.parameters());

        String sql;
        // Check if length is specified (not null)
        if (substring.getLength() instanceof NullScalarExpression) {
            // SUBSTRING with only expression and start position
            sql = String.format("SUBSTRING(%s, %s)", expressionResult.sql(), startPositionResult.sql());
        } else {
            // SUBSTRING with expression, start position and length
            PsDto lengthResult = substring.getLength().accept(renderer, ctx);
            parameters.addAll(lengthResult.parameters());
            sql = String.format(
                    "SUBSTRING(%s, %s, %s)", expressionResult.sql(), startPositionResult.sql(), lengthResult.sql());
        }

        return new PsDto(sql, parameters);
    }
}
