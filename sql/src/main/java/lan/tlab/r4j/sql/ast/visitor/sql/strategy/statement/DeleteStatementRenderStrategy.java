package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.statement.dml.DeleteStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class DeleteStatementRenderStrategy implements StatementRenderStrategy {

    public String render(DeleteStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "DELETE FROM %s %s",
                        statement.table().accept(sqlRenderer, ctx),
                        statement.where().accept(sqlRenderer, ctx))
                .trim();
    }
}
