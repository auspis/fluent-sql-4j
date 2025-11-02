package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;

public class StandardSqlIndexDefinitionPsStrategy implements IndexDefinitionPsStrategy {

    @Override
    public PsDto handle(IndexDefinition index, PreparedStatementRenderer renderer, AstContext ctx) {
        // Index definitions are static DDL elements without parameters
        // Use the SQL renderer from the PreparedStatementRenderer to ensure dialect consistency
        String sql = index.accept(renderer.getSqlRenderer(), ctx);
        return new PsDto(sql, List.of());
    }
}
