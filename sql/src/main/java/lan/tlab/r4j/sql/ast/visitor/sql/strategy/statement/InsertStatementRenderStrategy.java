package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.dml.InsertStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class InsertStatementRenderStrategy implements StatementRenderStrategy {

    public String render(InsertStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        String columnNames = statement.columns().stream()
                .map(column -> column.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));

        if (!columnNames.isEmpty()) {
            columnNames = String.format(" (%s)", columnNames);
        }

        return String.format(
                "INSERT INTO %s%s %s",
                statement.table().accept(sqlRenderer, ctx),
                columnNames,
                statement.data().accept(sqlRenderer, ctx));
    }
}
