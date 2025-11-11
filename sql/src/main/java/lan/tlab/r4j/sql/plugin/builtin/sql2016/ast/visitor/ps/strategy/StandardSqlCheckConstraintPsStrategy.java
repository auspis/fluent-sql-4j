package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.sql.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CheckConstraintPsStrategy;

public class StandardSqlCheckConstraintPsStrategy implements CheckConstraintPsStrategy {

    @Override
    public PsDto handle(CheckConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // Check constraints are static DDL elements without parameters
        // Use the SQL renderer from the PreparedStatementRenderer to ensure dialect consistency
        String sql = constraint.accept(renderer.getSqlRenderer(), ctx);
        return new PsDto(sql, List.of());
    }
}
