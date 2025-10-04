package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.statement;

import lan.tlab.sqlbuilder.ast.statement.DeleteStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class DeleteStatementRenderStrategy implements StatementRenderStrategy {

    public String render(DeleteStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "DELETE FROM %s %s",
                        statement.getTable().accept(sqlRenderer, ctx),
                        statement.getWhere().accept(sqlRenderer, ctx))
                .trim();
    }
}
