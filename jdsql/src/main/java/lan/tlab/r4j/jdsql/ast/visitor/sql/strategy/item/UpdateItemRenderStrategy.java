package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface UpdateItemRenderStrategy extends SqlItemRenderStrategy {
    String render(UpdateItem item, SqlRenderer sqlRenderer, AstContext ctx);
}
