package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.UpdateItem;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class UpdateItemRenderStrategy implements SqlItemRenderStrategy {

    public String render(UpdateItem item, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s = %s",
                item.getColumn().accept(sqlRenderer, ctx), item.getValue().accept(sqlRenderer, ctx));
    }
}
