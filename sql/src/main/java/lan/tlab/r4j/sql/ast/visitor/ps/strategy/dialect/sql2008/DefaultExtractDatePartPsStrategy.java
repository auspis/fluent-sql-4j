package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ExtractDatePartPsStrategy;

public class DefaultExtractDatePartPsStrategy implements ExtractDatePartPsStrategy {

    @Override
    public PsDto handle(ExtractDatePart extractDatePart, PreparedStatementRenderer renderer, AstContext ctx) {
        var dateExpressionResult = extractDatePart.getDateExpression().accept(renderer, ctx);
        String functionName = extractDatePart.getFunctionName();

        String sql = String.format("EXTRACT(%s FROM %s)", functionName, dateExpressionResult.sql());
        return new PsDto(sql, dateExpressionResult.parameters());
    }
}
