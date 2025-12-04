package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CreateTableStatementPsStrategy;

public class StandardSqlCreateTableStatementPsStrategy implements CreateTableStatementPsStrategy {

    @Override
    public PsDto handle(CreateTableStatement createTableStatement, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto tableDefinitionDto = createTableStatement.tableDefinition().accept(renderer, ctx);
        String sql = "CREATE TABLE " + tableDefinitionDto.sql();
        return new PsDto(sql, tableDefinitionDto.parameters());
    }
}
