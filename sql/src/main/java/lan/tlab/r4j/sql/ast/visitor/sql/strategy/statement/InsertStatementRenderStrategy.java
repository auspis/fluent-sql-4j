package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.InsertStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

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
