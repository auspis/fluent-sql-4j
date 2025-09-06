package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import lan.tlab.sqlbuilder.ast.statement.DeleteStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class DeleteStatementRenderStrategy implements StatementRenderStrategy {

    public String render(DeleteStatement statement, SqlRenderer sqlRenderer) {
        return String.format(
                        "DELETE FROM %s %s",
                        statement.getTable().accept(sqlRenderer),
                        statement.getWhere().accept(sqlRenderer))
                .trim();
    }
}
