package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Round;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.RoundPsStrategy;

public class StandardSqlRoundPsStrategy implements RoundPsStrategy {

    @Override
    public PsDto handle(Round round, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto numericResult = round.numericExpression().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>(numericResult.parameters());
        String sql;

        // Check if decimalPlaces is specified (not null)
        if (round.decimalPlaces() instanceof NullScalarExpression) {
            // ROUND with only one parameter
            sql = String.format("ROUND(%s)", numericResult.sql());
        } else {
            // ROUND with two parameters
            PsDto decimalPlacesResult = round.decimalPlaces().accept(renderer, ctx);
            parameters.addAll(decimalPlacesResult.parameters());
            sql = String.format("ROUND(%s, %s)", numericResult.sql(), decimalPlacesResult.sql());
        }

        return new PsDto(sql, parameters);
    }
}
