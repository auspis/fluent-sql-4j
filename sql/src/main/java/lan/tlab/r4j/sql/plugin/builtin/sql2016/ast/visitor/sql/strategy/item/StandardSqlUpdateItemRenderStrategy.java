package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.UpdateItemRenderStrategy;

public class StandardSqlUpdateItemRenderStrategy implements UpdateItemRenderStrategy {

    @Override
    public String render(UpdateItem item, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s = %s", item.column().accept(sqlRenderer, ctx), item.value().accept(sqlRenderer, ctx));
    }
}
