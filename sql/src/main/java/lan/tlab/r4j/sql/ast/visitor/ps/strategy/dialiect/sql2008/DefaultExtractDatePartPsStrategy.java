package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ExtractDatePartPsStrategy;

public class DefaultExtractDatePartPsStrategy implements ExtractDatePartPsStrategy {

    @Override
    public PsDto handle(ExtractDatePart extractDatePart, PreparedStatementVisitor visitor, AstContext ctx) {
        var dateExpressionResult = extractDatePart.getDateExpression().accept(visitor, ctx);
        String functionName = extractDatePart.getFunctionName();

        String sql = String.format("EXTRACT(%s FROM %s)", functionName, dateExpressionResult.sql());
        return new PsDto(sql, dateExpressionResult.parameters());
    }
}
