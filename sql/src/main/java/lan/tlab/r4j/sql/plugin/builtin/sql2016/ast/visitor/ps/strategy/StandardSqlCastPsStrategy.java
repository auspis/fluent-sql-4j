package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.Cast;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CastPsStrategy;

public class StandardSqlCastPsStrategy implements CastPsStrategy {

    @Override
    public PsDto handle(Cast cast, PreparedStatementRenderer renderer, AstContext ctx) {
        // Visit the inner expression to get its SQL and parameters
        PsDto expressionDto = cast.expression().accept(renderer, ctx);

        // Build the CAST SQL syntax
        String sql = "CAST(" + expressionDto.sql() + " AS " + cast.dataType() + ")";

        // Return the parameters from the inner expression
        return new PsDto(sql, expressionDto.parameters());
    }
}
