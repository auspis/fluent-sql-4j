package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.ExtractDatePart;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.ExtractDatePartPsStrategy;

public class StandardSqlExtractDatePartPsStrategy implements ExtractDatePartPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ExtractDatePart extractDatePart, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var dateExpressionResult = extractDatePart.dateExpression().accept(astToPsSpecVisitor, ctx);
        String functionName = extractDatePart.functionName().name();

        String sql = String.format("EXTRACT(%s FROM %s)", functionName, dateExpressionResult.sql());
        return new PreparedStatementSpec(sql, dateExpressionResult.parameters());
    }
}
