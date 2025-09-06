package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class InsertValueRenderStrategy implements SqlItemRenderStrategy {

    public String render(InsertValues item, SqlRenderer sqlRenderer) {
        String values = item.getValueExpressions().stream()
                .map(value -> value.accept(sqlRenderer))
                .collect(Collectors.joining(", "));
        return values.isEmpty() ? "" : String.format("VALUES (%s)", values);
    }
}
