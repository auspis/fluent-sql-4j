package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.Collections;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class DefaultReferencesItemPsStrategy implements ReferencesItemPsStrategy {

    @Override
    public PsDto handle(ReferencesItem item, PreparedStatementVisitor visitor, AstContext ctx) {
        EscapeStrategy escapeStrategy = visitor.getEscapeStrategy();

        String sql = String.format(
                "REFERENCES %s (%s)",
                escapeStrategy.apply(item.getTable()),
                item.getColumns().stream().map(escapeStrategy::apply).collect(Collectors.joining(", ")));

        return new PsDto(sql, Collections.emptyList());
    }
}
