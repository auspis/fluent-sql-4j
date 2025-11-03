package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface UpdateItemRenderStrategy extends SqlItemRenderStrategy {
    String render(UpdateItem item, SqlRenderer sqlRenderer, AstContext ctx);
}
