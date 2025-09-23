package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.TableDefinitionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;

public class DefaultTableDefinitionPsStrategy implements TableDefinitionPsStrategy {

    @Override
    public PsDto handle(TableDefinition tableDefinition, PreparedStatementVisitor visitor, AstContext ctx) {
        // For table definitions, we use the SQL renderer since DDL definitions typically don't have parameters
        // The table name itself might have parameters if it's a complex expression, but column definitions are static
        PsDto tableDto = tableDefinition.getTable().accept(visitor, ctx);

        // Generate the column definitions using SQL renderer
        String columnsSql = SqlRendererFactory.standardSql2008().visit(tableDefinition, ctx);

        // Extract only the table definition part (everything after the table name)
        int openParenIndex = columnsSql.indexOf(" (");
        String definitionPart = openParenIndex >= 0 ? columnsSql.substring(openParenIndex) : " ()";

        String sql = tableDto.sql() + definitionPart;

        return new PsDto(sql, tableDto.parameters());
    }
}
