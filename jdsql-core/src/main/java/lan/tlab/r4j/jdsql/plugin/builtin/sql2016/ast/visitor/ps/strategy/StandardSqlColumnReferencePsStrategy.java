package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class StandardSqlColumnReferencePsStrategy implements ColumnReferencePsStrategy {
    @Override
    public PsDto handle(ColumnReference col, Visitor<PsDto> renderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();
        if (renderer instanceof PreparedStatementRenderer psRenderer) {
            escapeStrategy = psRenderer.getEscapeStrategy();
        }

        boolean qualify = ctx.hasFeature(AstContext.Feature.JOIN_ON) || ctx.hasFeature(AstContext.Feature.UNION);
        String sql;
        if (qualify && !col.table().isBlank()) {
            sql = escapeStrategy.apply(col.table()) + "." + escapeStrategy.apply(col.column());
        } else {
            sql = escapeStrategy.apply(col.column());
        }
        return new PsDto(sql, List.of());
    }
}
