package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface InsertValueRenderStrategy extends SqlItemRenderStrategy {
    String render(InsertValues item, SqlRenderer sqlRenderer, AstContext ctx);
}
