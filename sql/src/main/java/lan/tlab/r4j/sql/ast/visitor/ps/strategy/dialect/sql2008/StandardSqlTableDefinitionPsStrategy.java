package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TableDefinitionPsStrategy;

public class StandardSqlTableDefinitionPsStrategy implements TableDefinitionPsStrategy {

    @Override
    public PsDto handle(TableDefinition tableDefinition, PreparedStatementRenderer renderer, AstContext ctx) {
        // For table definitions, we use the SQL renderer since DDL definitions typically don't have parameters
        // The table name itself might have parameters if it's a complex expression, but column definitions are static
        PsDto tableDto = tableDefinition.table().accept(renderer, ctx);

        // Generate the column definitions using the SQL renderer from the PreparedStatementRenderer
        // This ensures we use the same dialect configuration (MySQL, PostgreSQL, etc.)
        String columnsSql = tableDefinition.accept(renderer.getSqlRenderer(), ctx);

        // Extract only the table definition part (everything after the table name)
        int openParenIndex = columnsSql.indexOf(" (");
        String definitionPart = openParenIndex >= 0 ? columnsSql.substring(openParenIndex) : " ()";

        String sql = tableDto.sql() + definitionPart;

        return new PsDto(sql, tableDto.parameters());
    }
}
