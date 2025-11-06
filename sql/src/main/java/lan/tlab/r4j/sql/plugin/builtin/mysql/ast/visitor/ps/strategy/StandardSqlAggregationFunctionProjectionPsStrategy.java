package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AggregationFunctionProjectionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class StandardSqlAggregationFunctionProjectionPsStrategy implements AggregationFunctionProjectionPsStrategy {
    @Override
    public PsDto handle(
            AggregateCallProjection aggregationFunctionProjection, Visitor<PsDto> renderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();
        if (renderer instanceof PreparedStatementRenderer psRenderer) {
            escapeStrategy = psRenderer.getEscapeStrategy();
        }

        // The AggregationFunctionProjection wraps an AggregateCall (e.g., COUNT, SUM, etc.)
        var expr = aggregationFunctionProjection.expression();
        PsDto exprResult = expr.accept(renderer, ctx);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = aggregationFunctionProjection.as() != null
                ? aggregationFunctionProjection.as().name()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + escapeStrategy.apply(alias);
        }
        return new PsDto(sql, exprResult.parameters());
    }
}
