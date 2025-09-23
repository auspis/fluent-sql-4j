package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.CastPsStrategy;

public class DefaultCastPsStrategy implements CastPsStrategy {

    @Override
    public PsDto handle(Cast cast, PreparedStatementVisitor visitor, AstContext ctx) {
        // Visit the inner expression to get its SQL and parameters
        PsDto expressionDto = cast.getExpression().accept(visitor, ctx);

        // Build the CAST SQL syntax
        String sql = "CAST(" + expressionDto.sql() + " AS " + cast.getDataType() + ")";

        // Return the parameters from the inner expression
        return new PsDto(sql, expressionDto.parameters());
    }
}
