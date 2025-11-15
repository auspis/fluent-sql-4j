package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface WhenMatchedUpdateRenderStrategy extends SqlItemRenderStrategy {
    String render(WhenMatchedUpdate action, SqlRenderer sqlRenderer, AstContext ctx);
}
