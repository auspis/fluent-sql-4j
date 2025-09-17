package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultTablePsStrategy implements TablePsStrategy {
    @Override
    public PsDto handle(Table table, Visitor<PsDto> visitor, AstContext ctx) {
        String sql = "\"" + table.getName() + "\"";
        String alias = table.getAs() != null ? table.getAs().getName() : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + alias;
        }
        return new PsDto(sql, List.of());
    }
}
