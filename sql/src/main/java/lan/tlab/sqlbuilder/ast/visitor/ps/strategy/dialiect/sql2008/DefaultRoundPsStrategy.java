package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Round;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.RoundPsStrategy;

public class DefaultRoundPsStrategy implements RoundPsStrategy {

    @Override
    public PsDto handle(Round round, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto numericResult = round.getNumericExpression().accept(visitor, ctx);

        List<Object> parameters = new ArrayList<>(numericResult.parameters());
        String sql;

        // Check if decimalPlaces is specified (not null)
        if (round.getDecimalPlaces() instanceof NullScalarExpression) {
            // ROUND with only one parameter
            sql = String.format("ROUND(%s)", numericResult.sql());
        } else {
            // ROUND with two parameters
            PsDto decimalPlacesResult = round.getDecimalPlaces().accept(visitor, ctx);
            parameters.addAll(decimalPlacesResult.parameters());
            sql = String.format("ROUND(%s, %s)", numericResult.sql(), decimalPlacesResult.sql());
        }

        return new PsDto(sql, parameters);
    }
}
