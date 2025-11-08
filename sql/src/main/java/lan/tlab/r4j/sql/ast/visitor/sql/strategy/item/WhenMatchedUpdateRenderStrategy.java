package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface WhenMatchedUpdateRenderStrategy extends SqlItemRenderStrategy {
    String render(WhenMatchedUpdate action, SqlRenderer sqlRenderer, AstContext ctx);
}
