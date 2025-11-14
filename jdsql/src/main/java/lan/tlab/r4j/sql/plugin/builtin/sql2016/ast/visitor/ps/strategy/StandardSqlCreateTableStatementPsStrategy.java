package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.Collections;
import lan.tlab.r4j.sql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CreateTableStatementPsStrategy;

public class StandardSqlCreateTableStatementPsStrategy implements CreateTableStatementPsStrategy {

    @Override
    public PsDto handle(CreateTableStatement createTableStatement, PreparedStatementRenderer renderer, AstContext ctx) {
        // For CREATE TABLE statements, we use the SQL renderer from the PreparedStatementRenderer
        // This ensures we use the same dialect configuration
        String sql = String.format(
                "CREATE TABLE %s", createTableStatement.tableDefinition().accept(renderer.getSqlRenderer(), ctx));

        return new PsDto(sql, Collections.emptyList());
    }
}
