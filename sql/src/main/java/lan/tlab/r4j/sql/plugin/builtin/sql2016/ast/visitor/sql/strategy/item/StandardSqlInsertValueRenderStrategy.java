package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.InsertValueRenderStrategy;

public class StandardSqlInsertValueRenderStrategy implements InsertValueRenderStrategy {

    @Override
    public String render(InsertValues item, SqlRenderer sqlRenderer, AstContext ctx) {
        String values = item.valueExpressions().stream()
                .map(value -> value.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));
        return values.isEmpty() ? "" : String.format("VALUES (%s)", values);
    }
}
