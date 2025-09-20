package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.statement;

import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class CreateTableStatementRenderStrategy implements StatementRenderStrategy {

    public String render(CreateTableStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("CREATE TABLE %s", statement.getTableDefinition().accept(sqlRenderer, ctx));
    }
}
