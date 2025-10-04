package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TablePsStrategy;

public class DefaultTablePsStrategy implements TablePsStrategy {
    @Override
    public PsDto handle(Table table, Visitor<PsDto> visitor, AstContext ctx) {
        PreparedStatementVisitor psVisitor = (PreparedStatementVisitor) visitor;
        String sql = psVisitor.getEscapeStrategy().apply(table.getName());
        String alias = table.getAs() != null ? table.getAs().getName() : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + alias;
        }
        return new PsDto(sql, List.of());
    }
}
