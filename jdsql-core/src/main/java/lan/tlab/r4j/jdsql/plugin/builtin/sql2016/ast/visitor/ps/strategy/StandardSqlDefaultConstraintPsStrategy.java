package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;

public class StandardSqlDefaultConstraintPsStrategy implements DefaultConstraintPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            DefaultConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // Default constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlDefaultConstraintRenderStrategy
        PreparedStatementSpec valueDto = constraint.value().accept(renderer, ctx);
        String sql = String.format("DEFAULT %s", valueDto.sql());
        return new PreparedStatementSpec(sql, valueDto.parameters());
    }
}
