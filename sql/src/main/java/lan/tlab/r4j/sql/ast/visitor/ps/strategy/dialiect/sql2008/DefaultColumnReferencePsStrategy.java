package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class DefaultColumnReferencePsStrategy implements ColumnReferencePsStrategy {
    @Override
    public PsDto handle(ColumnReference col, Visitor<PsDto> visitor, AstContext ctx) {
        EscapeStrategy escapeStrategy = EscapeStrategy.standard();
        if (visitor instanceof PreparedStatementVisitor psVisitor) {
            escapeStrategy = psVisitor.getEscapeStrategy();
        }

        boolean qualify = ctx.getScope() == AstContext.Scope.JOIN_ON || ctx.getScope() == AstContext.Scope.UNION;
        String sql;
        if (qualify && !col.getTable().isBlank()) {
            sql = escapeStrategy.apply(col.getTable()) + "." + escapeStrategy.apply(col.getColumn());
        } else {
            sql = escapeStrategy.apply(col.getColumn());
        }
        return new PsDto(sql, List.of());
    }
}
