package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.dml.component.MergeAction.WhenMatchedDelete;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface WhenMatchedDeleteRenderStrategy extends SqlItemRenderStrategy {
    String render(WhenMatchedDelete action, SqlRenderer sqlRenderer, AstContext ctx);
}
