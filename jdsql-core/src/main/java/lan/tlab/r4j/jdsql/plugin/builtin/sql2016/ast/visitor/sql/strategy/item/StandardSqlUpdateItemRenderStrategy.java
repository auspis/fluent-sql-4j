package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.UpdateItemRenderStrategy;

public class StandardSqlUpdateItemRenderStrategy implements UpdateItemRenderStrategy {

    @Override
    public String render(UpdateItem item, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s = %s", item.column().accept(sqlRenderer, ctx), item.value().accept(sqlRenderer, ctx));
    }
}
