package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DateArithmeticPsStrategy;

public class StandardSqlDateArithmeticPsStrategy implements DateArithmeticPsStrategy {

    @Override
    public PsDto handle(DateArithmetic dateArithmetic, PreparedStatementRenderer renderer, AstContext ctx) {
        var dateExpressionResult = dateArithmetic.dateExpression().accept(renderer, ctx);
        var intervalResult = dateArithmetic.interval().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(intervalResult.parameters());
        parameters.addAll(dateExpressionResult.parameters());

        String operation = dateArithmetic.isAddition() ? "DATEADD" : "DATESUB";
        String sql = String.format("%s(%s, %s)", operation, intervalResult.sql(), dateExpressionResult.sql());

        return new PsDto(sql, parameters);
    }
}
