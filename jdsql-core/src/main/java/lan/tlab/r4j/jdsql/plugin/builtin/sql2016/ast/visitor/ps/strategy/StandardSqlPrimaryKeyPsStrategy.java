package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;

public class StandardSqlPrimaryKeyPsStrategy implements PrimaryKeyPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            PrimaryKeyDefinition item, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        // Primary key constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlPrimaryKeyRenderStrategy
        String columns = item.columns().stream()
                .map(c -> renderer.getEscapeStrategy().apply(c))
                .collect(java.util.stream.Collectors.joining(", "));
        String sql = String.format("PRIMARY KEY (%s)", columns);
        return new PreparedStatementSpec(sql, List.of());
    }
}
