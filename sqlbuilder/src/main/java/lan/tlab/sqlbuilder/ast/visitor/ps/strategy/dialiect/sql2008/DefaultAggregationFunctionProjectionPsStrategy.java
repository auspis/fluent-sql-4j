package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class DefaultAggregationFunctionProjectionPsStrategy implements AggregationFunctionProjectionPsStrategy {
    @Override
    public PsDto handle(
            AggregationFunctionProjection aggregationFunctionProjection, Visitor<PsDto> visitor, AstContext ctx) {
        EscapeStrategy escapeStrategy = EscapeStrategy.standard();
        if (visitor instanceof PreparedStatementVisitor psVisitor) {
            escapeStrategy = psVisitor.getEscapeStrategy();
        }

        // The AggregationFunctionProjection wraps an AggregateCall (e.g., COUNT, SUM, etc.)
        var expr = aggregationFunctionProjection.getExpression();
        PsDto exprResult = expr.accept(visitor, ctx);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = aggregationFunctionProjection.getAs() != null
                ? aggregationFunctionProjection.getAs().getName()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + escapeStrategy.apply(alias);
        }
        return new PsDto(sql, exprResult.parameters());
    }
}
