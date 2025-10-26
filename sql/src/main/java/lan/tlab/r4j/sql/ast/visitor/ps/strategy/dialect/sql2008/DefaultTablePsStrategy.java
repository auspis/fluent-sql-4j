package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TablePsStrategy;

public class DefaultTablePsStrategy implements TablePsStrategy {
    @Override
    public PsDto handle(TableIdentifier table, Visitor<PsDto> renderer, AstContext ctx) {
        PreparedStatementRenderer psRenderer = (PreparedStatementRenderer) renderer;
        String sql = psRenderer.getEscapeStrategy().apply(table.getName());
        String alias = table.getAs() != null ? table.getAs().name() : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + alias;
        }
        return new PsDto(sql, List.of());
    }
}
