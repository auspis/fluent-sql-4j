package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.ExtractDatePart;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ExtractDatePartPsStrategy;

public class StandardSqlExtractDatePartPsStrategy implements ExtractDatePartPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ExtractDatePart extractDatePart, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        var dateExpressionResult = extractDatePart.dateExpression().accept(renderer, ctx);
        String functionName = extractDatePart.functionName().name();

        String sql = String.format("EXTRACT(%s FROM %s)", functionName, dateExpressionResult.sql());
        return new PreparedStatementSpec(sql, dateExpressionResult.parameters());
    }
}
