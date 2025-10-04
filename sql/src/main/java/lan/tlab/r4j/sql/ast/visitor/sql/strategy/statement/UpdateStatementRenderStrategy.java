package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.UpdateStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class UpdateStatementRenderStrategy implements StatementRenderStrategy {

    public String render(UpdateStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        String setList = statement.getSet().stream()
                .map(item -> sqlRenderer.visit(item, ctx))
                .collect(Collectors.joining(", "));
        return String.format(
                        "UPDATE %s SET %s %s",
                        statement.getTable().accept(sqlRenderer, ctx),
                        setList,
                        statement.getWhere().accept(sqlRenderer, ctx))
                .trim();
    }
}
