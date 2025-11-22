package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.MergeUsing;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface MergeUsingRenderStrategy extends SqlItemRenderStrategy {
    String render(MergeUsing using, SqlRenderer sqlRenderer, AstContext ctx);
}
