package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class StandardSqlColumnReferencePsStrategy implements ColumnReferencePsStrategy {
    @Override
    public PsDto handle(ColumnReference col, Visitor<PsDto> renderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = EscapeStrategy.standard();
        if (renderer instanceof PreparedStatementRenderer psRenderer) {
            escapeStrategy = psRenderer.getEscapeStrategy();
        }

        boolean qualify = ctx.scope() == AstContext.Scope.JOIN_ON || ctx.scope() == AstContext.Scope.UNION;
        String sql;
        if (qualify && !col.table().isBlank()) {
            sql = escapeStrategy.apply(col.table()) + "." + escapeStrategy.apply(col.column());
        } else {
            sql = escapeStrategy.apply(col.column());
        }
        return new PsDto(sql, List.of());
    }
}
