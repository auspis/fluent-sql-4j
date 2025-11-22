package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.dml.statement.UpdateStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.UpdateStatementRenderStrategy;

public class StandardSqlUpdateStatementRenderStrategy implements UpdateStatementRenderStrategy {

    public String render(UpdateStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        String setList = statement.set().stream()
                .map(item -> sqlRenderer.visit(item, ctx))
                .collect(Collectors.joining(", "));
        return String.format(
                        "UPDATE %s SET %s %s",
                        statement.table().accept(sqlRenderer, ctx),
                        setList,
                        statement.where().accept(sqlRenderer, ctx))
                .trim();
    }
}
