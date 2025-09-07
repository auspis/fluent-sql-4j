package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class CreateTableStatementRenderStrategy implements StatementRenderStrategy {

    public String render(CreateTableStatement statement, SqlRenderer sqlRenderer) {
        return String.format("CREATE TABLE %s", statement.getTableDefinition().accept(sqlRenderer));
    }
}
