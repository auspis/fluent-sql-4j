package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.dml.component.MergeUsing;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface MergeUsingRenderStrategy extends SqlItemRenderStrategy {
    String render(MergeUsing using, SqlRenderer sqlRenderer, AstContext ctx);
}
