package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.convert.Cast;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CastPsStrategy;

public class DefaultCastPsStrategy implements CastPsStrategy {

    @Override
    public PsDto handle(Cast cast, PreparedStatementRenderer renderer, AstContext ctx) {
        // Visit the inner expression to get its SQL and parameters
        PsDto expressionDto = cast.getExpression().accept(renderer, ctx);

        // Build the CAST SQL syntax
        String sql = "CAST(" + expressionDto.sql() + " AS " + cast.getDataType() + ")";

        // Return the parameters from the inner expression
        return new PsDto(sql, expressionDto.parameters());
    }
}
