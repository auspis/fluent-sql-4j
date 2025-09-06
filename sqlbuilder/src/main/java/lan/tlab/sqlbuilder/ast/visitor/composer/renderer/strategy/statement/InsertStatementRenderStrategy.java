package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class InsertStatementRenderStrategy implements StatementRenderStrategy {

    public String render(InsertStatement statement, SqlRenderer sqlRenderer) {
        String columnNames = statement.getColumns().stream()
                .map(column -> column.accept(sqlRenderer))
                .collect(Collectors.joining(", "));

        if (!columnNames.isEmpty()) {
            columnNames = String.format(" (%s)", columnNames);
        }

        return String.format(
                "INSERT INTO %s%s %s",
                statement.getTable().accept(sqlRenderer),
                columnNames,
                statement.getData().accept(sqlRenderer));
    }
}
