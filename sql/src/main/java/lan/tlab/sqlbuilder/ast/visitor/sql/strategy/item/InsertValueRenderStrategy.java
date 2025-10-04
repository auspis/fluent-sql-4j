package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class InsertValueRenderStrategy implements SqlItemRenderStrategy {

    public String render(InsertValues item, SqlRenderer sqlRenderer, AstContext ctx) {
        String values = item.getValueExpressions().stream()
                .map(value -> value.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));
        return values.isEmpty() ? "" : String.format("VALUES (%s)", values);
    }
}
