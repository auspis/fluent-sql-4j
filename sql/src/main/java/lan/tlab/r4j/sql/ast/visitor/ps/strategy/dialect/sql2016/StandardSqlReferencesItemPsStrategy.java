package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.Collections;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class StandardSqlReferencesItemPsStrategy implements ReferencesItemPsStrategy {

    @Override
    public PsDto handle(ReferencesItem item, PreparedStatementRenderer renderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();

        String sql = String.format(
                "REFERENCES %s (%s)",
                escapeStrategy.apply(item.table()),
                item.columns().stream().map(escapeStrategy::apply).collect(Collectors.joining(", ")));

        return new PsDto(sql, Collections.emptyList());
    }
}
