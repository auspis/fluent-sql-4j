package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;

public class StandardSqlNotNullConstraintPsStrategy implements NotNullConstraintPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            NotNullConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // NOT NULL constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlNotNullConstraintRenderStrategy
        return new PreparedStatementSpec("NOT NULL", List.of());
    }
}
