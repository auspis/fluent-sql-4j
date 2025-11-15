package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.jdsql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.CreateTableStatementRenderStrategy;

public class StandardSqlCreateTableStatementRenderStrategy implements CreateTableStatementRenderStrategy {

    @Override
    public String render(CreateTableStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("CREATE TABLE %s", statement.tableDefinition().accept(sqlRenderer, ctx));
    }
}
