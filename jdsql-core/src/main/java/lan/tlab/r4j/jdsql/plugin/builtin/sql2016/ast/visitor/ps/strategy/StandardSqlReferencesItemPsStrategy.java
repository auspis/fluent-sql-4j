package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.Collections;
import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.EscapeStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ReferencesItemPsStrategy;

public class StandardSqlReferencesItemPsStrategy implements ReferencesItemPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ReferencesItem item, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();

        String sql = String.format(
                "REFERENCES %s (%s)",
                escapeStrategy.apply(item.table()),
                item.columns().stream().map(escapeStrategy::apply).collect(Collectors.joining(", ")));

        return new PreparedStatementSpec(sql, Collections.emptyList());
    }
}
