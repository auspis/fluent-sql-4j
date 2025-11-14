package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.sql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;

public class StandardSqlNotNullConstraintPsStrategy implements NotNullConstraintPsStrategy {

    @Override
    public PsDto handle(NotNullConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // NotNull constraints are static DDL elements without parameters
        // Use the SQL renderer from the PreparedStatementRenderer to ensure dialect consistency
        String sql = constraint.accept(renderer.getSqlRenderer(), ctx);
        return new PsDto(sql, List.of());
    }
}
