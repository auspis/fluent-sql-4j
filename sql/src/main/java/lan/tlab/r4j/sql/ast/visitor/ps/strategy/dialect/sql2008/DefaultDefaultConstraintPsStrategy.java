package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;

public class DefaultDefaultConstraintPsStrategy implements DefaultConstraintPsStrategy {

    @Override
    public PsDto handle(DefaultConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // Default constraints are static DDL elements without parameters
        // Use the SQL renderer from the PreparedStatementRenderer to ensure dialect consistency
        String sql = constraint.accept(renderer.getSqlRenderer(), ctx);
        return new PsDto(sql, List.of());
    }
}
