package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;

public class StandardSqlForeignKeyConstraintPsStrategy implements ForeignKeyConstraintPsStrategy {

    @Override
    public PsDto handle(ForeignKeyConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // Foreign key constraints are static DDL elements without parameters
        // Use the SQL renderer from the PreparedStatementRenderer to ensure dialect consistency
        String sql = constraint.accept(renderer.getSqlRenderer(), ctx);
        return new PsDto(sql, List.of());
    }
}
