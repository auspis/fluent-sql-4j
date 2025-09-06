package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class CreateTableStatementRenderStrategy implements StatementRenderStrategy {

    public String render(CreateTableStatement statement, SqlRenderer sqlRenderer) {
        // TODO aaa - refactor and test
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        builder.append(statement.getTableDefinition().accept(sqlRenderer));
        return builder.toString();
    }
}
