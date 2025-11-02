package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;

public class StandardSqlPrimaryKeyPsStrategy implements PrimaryKeyPsStrategy {

    @Override
    public PsDto handle(PrimaryKeyDefinition item, PreparedStatementRenderer renderer, AstContext ctx) {
        // PrimaryKey constraints are static DDL elements without parameters
        // Use the SQL renderer from the PreparedStatementRenderer to ensure dialect consistency
        String sql = item.accept(renderer.getSqlRenderer(), ctx);
        return new PsDto(sql, List.of());
    }
}
