package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DateArithmeticPsStrategy;

public class DefaultDateArithmeticPsStrategy implements DateArithmeticPsStrategy {

    @Override
    public PsDto handle(DateArithmetic dateArithmetic, PreparedStatementVisitor visitor, AstContext ctx) {
        var dateExpressionResult = dateArithmetic.getDateExpression().accept(visitor, ctx);
        var intervalResult = dateArithmetic.getInterval().accept(visitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(intervalResult.parameters());
        parameters.addAll(dateExpressionResult.parameters());

        String operation = dateArithmetic.isAdd() ? "DATEADD" : "DATESUB";
        String sql = String.format("%s(%s, %s)", operation, intervalResult.sql(), dateExpressionResult.sql());

        return new PsDto(sql, parameters);
    }
}
