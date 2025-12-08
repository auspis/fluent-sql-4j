package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;

public class StandardSqlUniqueConstraintPsStrategy implements UniqueConstraintPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            UniqueConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // Unique constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlUniqueConstraintRenderStrategy
        String columns = constraint.columns().stream()
                .map(c -> renderer.getEscapeStrategy().apply(c))
                .collect(java.util.stream.Collectors.joining(", "));
        String sql = String.format("UNIQUE (%s)", columns);
        return new PreparedStatementSpec(sql, List.of());
    }
}
