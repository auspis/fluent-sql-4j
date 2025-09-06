package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class TableRenderStrategy implements SqlItemRenderStrategy {

    public String render(Table table, SqlRenderer sqlRenderer) {
        String alias = table.getAs().accept(sqlRenderer);
        if (!alias.isEmpty()) {
            alias = " " + alias;
        }
        return sqlRenderer.getEscapeStrategy().apply(table.getName()) + alias;
    }
}
