package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.DateArithmetic;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.DateArithmeticPsStrategy;
import java.util.ArrayList;
import java.util.List;

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
