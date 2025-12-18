package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.IndexDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;

public class StandardSqlIndexDefinitionPsStrategy implements IndexDefinitionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            IndexDefinition index, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        // Index definitions are static DDL elements without parameters
        // Inline rendering logic from StandardSqlIndexDefinitionRenderStrategy
        String sql = String.format(
                "INDEX %s (%s)",
                renderer.getEscapeStrategy().apply(index.name()),
                index.columnNames().stream()
                        .map(renderer.getEscapeStrategy()::apply)
                        .collect(java.util.stream.Collectors.joining(", ")));
        return new PreparedStatementSpec(sql, List.of());
    }
}
