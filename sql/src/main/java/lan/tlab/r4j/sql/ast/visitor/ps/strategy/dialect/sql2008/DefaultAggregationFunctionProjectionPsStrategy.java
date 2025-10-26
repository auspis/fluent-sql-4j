package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class DefaultAggregationFunctionProjectionPsStrategy implements AggregationFunctionProjectionPsStrategy {
    @Override
    public PsDto handle(
            AggregateCallProjection aggregationFunctionProjection, Visitor<PsDto> renderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = EscapeStrategy.standard();
        if (renderer instanceof PreparedStatementRenderer psRenderer) {
            escapeStrategy = psRenderer.getEscapeStrategy();
        }

        // The AggregationFunctionProjection wraps an AggregateCall (e.g., COUNT, SUM, etc.)
        var expr = aggregationFunctionProjection.getExpression();
        PsDto exprResult = expr.accept(renderer, ctx);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = aggregationFunctionProjection.getAs() != null
                ? aggregationFunctionProjection.getAs().name()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + escapeStrategy.apply(alias);
        }
        return new PsDto(sql, exprResult.parameters());
    }
}
