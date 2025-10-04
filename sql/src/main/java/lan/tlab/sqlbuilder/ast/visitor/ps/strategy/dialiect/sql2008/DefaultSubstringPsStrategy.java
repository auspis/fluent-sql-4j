package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SubstringPsStrategy;

public class DefaultSubstringPsStrategy implements SubstringPsStrategy {

    @Override
    public PsDto handle(Substring substring, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto expressionResult = substring.getExpression().accept(visitor, ctx);
        PsDto startPositionResult = substring.getStartPosition().accept(visitor, ctx);

        List<Object> parameters = new ArrayList<>(expressionResult.parameters());
        parameters.addAll(startPositionResult.parameters());

        String sql;
        // Check if length is specified (not null)
        if (substring.getLength() instanceof NullScalarExpression) {
            // SUBSTRING with only expression and start position
            sql = String.format("SUBSTRING(%s, %s)", expressionResult.sql(), startPositionResult.sql());
        } else {
            // SUBSTRING with expression, start position and length
            PsDto lengthResult = substring.getLength().accept(visitor, ctx);
            parameters.addAll(lengthResult.parameters());
            sql = String.format(
                    "SUBSTRING(%s, %s, %s)", expressionResult.sql(), startPositionResult.sql(), lengthResult.sql());
        }

        return new PsDto(sql, parameters);
    }
}
