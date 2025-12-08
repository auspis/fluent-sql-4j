package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.DateArithmetic;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DateArithmeticPsStrategy;

public class StandardSqlDateArithmeticPsStrategy implements DateArithmeticPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            DateArithmetic dateArithmetic, PreparedStatementRenderer renderer, AstContext ctx) {
        var dateExpressionResult = dateArithmetic.dateExpression().accept(renderer, ctx);
        var intervalResult = dateArithmetic.interval().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(intervalResult.parameters());
        parameters.addAll(dateExpressionResult.parameters());

        String operation = dateArithmetic.isAddition() ? "DATEADD" : "DATESUB";
        String sql = String.format("%s(%s, %s)", operation, intervalResult.sql(), dateExpressionResult.sql());

        return new PreparedStatementSpec(sql, parameters);
    }
}
