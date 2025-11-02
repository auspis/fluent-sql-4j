package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.statement.ddl.CreateTableStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.CreateTableStatementRenderStrategy;

public class StandardSqlCreateTableStatementRenderStrategy implements CreateTableStatementRenderStrategy {

    public String render(CreateTableStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("CREATE TABLE %s", statement.tableDefinition().accept(sqlRenderer, ctx));
    }
}
