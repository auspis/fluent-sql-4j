package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class TableRenderStrategy implements SqlItemRenderStrategy {

    public String render(Table table, SqlRenderer sqlRenderer, AstContext ctx) {
        String alias = table.getAs().accept(sqlRenderer, ctx);
        if (!alias.isEmpty()) {
            alias = " " + alias;
        }
        return sqlRenderer.getEscapeStrategy().apply(table.getName()) + alias;
    }
}
