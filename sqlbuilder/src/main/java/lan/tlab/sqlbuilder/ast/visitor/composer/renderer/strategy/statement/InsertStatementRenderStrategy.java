package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class InsertStatementRenderStrategy implements StatementRenderStrategy {

    public String render(InsertStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        String columnNames = statement.getColumns().stream()
                .map(column -> column.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));

        if (!columnNames.isEmpty()) {
            columnNames = String.format(" (%s)", columnNames);
        }

        return String.format(
                "INSERT INTO %s%s %s",
                statement.getTable().accept(sqlRenderer, ctx),
                columnNames,
                statement.getData().accept(sqlRenderer, ctx));
    }
}
