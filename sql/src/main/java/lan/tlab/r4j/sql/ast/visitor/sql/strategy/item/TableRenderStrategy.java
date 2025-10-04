package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class TableRenderStrategy implements SqlItemRenderStrategy {

    public String render(Table table, SqlRenderer sqlRenderer, AstContext ctx) {
        String alias = table.getAs().accept(sqlRenderer, ctx);
        if (!alias.isEmpty()) {
            alias = " " + alias;
        }
        return sqlRenderer.getEscapeStrategy().apply(table.getName()) + alias;
    }
}
