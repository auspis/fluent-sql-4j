package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultColumnReferencePsStrategy implements ColumnReferencePsStrategy {
    @Override
    public PsDto handle(ColumnReference col, Visitor<PsDto> visitor, AstContext ctx) {
        boolean qualify = ctx != null && ctx.getScope() == AstContext.Scope.JOIN_ON;
        String sql;
        if (qualify && col.getTable() != null && !col.getTable().isBlank()) {
            sql = "\"" + col.getTable() + "\".\"" + col.getColumn() + "\"";
        } else {
            sql = "\"" + col.getColumn() + "\"";
        }
        return new PsDto(sql, List.of());
    }
}
