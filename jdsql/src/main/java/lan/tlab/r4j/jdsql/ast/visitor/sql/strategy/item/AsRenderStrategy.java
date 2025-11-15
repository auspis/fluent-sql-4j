package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.common.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface AsRenderStrategy extends SqlItemRenderStrategy {

    String render(Alias as, SqlRenderer sqlRenderer, AstContext ctx);
}
