package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.DateArithmetic;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.DateArithmeticPsStrategy;

public class StandardSqlDateArithmeticPsStrategy implements DateArithmeticPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            DateArithmetic dateArithmetic, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var dateExpressionResult = dateArithmetic.dateExpression().accept(astToPsSpecVisitor, ctx);
        var intervalResult = dateArithmetic.interval().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(intervalResult.parameters());
        parameters.addAll(dateExpressionResult.parameters());

        String operation = dateArithmetic.isAddition() ? "DATEADD" : "DATESUB";
        String sql = String.format("%s(%s, %s)", operation, intervalResult.sql(), dateExpressionResult.sql());

        return new PreparedStatementSpec(sql, parameters);
    }
}
