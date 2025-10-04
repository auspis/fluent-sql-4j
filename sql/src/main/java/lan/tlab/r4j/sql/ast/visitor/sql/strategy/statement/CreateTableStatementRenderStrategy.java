package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.statement.ddl.CreateTableStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class CreateTableStatementRenderStrategy implements StatementRenderStrategy {

    public String render(CreateTableStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("CREATE TABLE %s", statement.getTableDefinition().accept(sqlRenderer, ctx));
    }
}
