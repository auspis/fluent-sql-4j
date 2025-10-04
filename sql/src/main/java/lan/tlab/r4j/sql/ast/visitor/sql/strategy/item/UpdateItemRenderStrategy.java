package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class UpdateItemRenderStrategy implements SqlItemRenderStrategy {

    public String render(UpdateItem item, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s = %s",
                item.getColumn().accept(sqlRenderer, ctx), item.getValue().accept(sqlRenderer, ctx));
    }
}
