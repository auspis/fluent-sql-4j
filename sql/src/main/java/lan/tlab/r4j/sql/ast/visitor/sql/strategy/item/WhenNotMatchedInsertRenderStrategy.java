package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface WhenNotMatchedInsertRenderStrategy extends SqlItemRenderStrategy {

    String render(WhenNotMatchedInsert action, SqlRenderer sqlRenderer, AstContext ctx);
}
