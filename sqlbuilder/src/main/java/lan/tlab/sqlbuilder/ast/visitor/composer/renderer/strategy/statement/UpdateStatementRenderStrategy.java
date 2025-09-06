package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.statement.UpdateStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class UpdateStatementRenderStrategy implements StatementRenderStrategy {

    public String render(UpdateStatement statement, SqlRenderer sqlRenderer) {
        String setList =
                statement.getSet().stream().map(item -> sqlRenderer.visit(item)).collect(Collectors.joining(", "));
        return String.format(
                        "UPDATE %s SET %s %s",
                        statement.getTable().accept(sqlRenderer),
                        setList,
                        statement.getWhere().accept(sqlRenderer))
                .trim();
    }
}
