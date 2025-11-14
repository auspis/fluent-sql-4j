package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.DeleteStatementRenderStrategy;

public class StandardSqlDeleteStatementRenderStrategy implements DeleteStatementRenderStrategy {

    @Override
    public String render(DeleteStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "DELETE FROM %s %s",
                        statement.table().accept(sqlRenderer, ctx),
                        statement.where().accept(sqlRenderer, ctx))
                .trim();
    }
}
