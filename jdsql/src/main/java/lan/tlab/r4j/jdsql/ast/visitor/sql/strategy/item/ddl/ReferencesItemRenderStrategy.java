package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface ReferencesItemRenderStrategy extends SqlItemRenderStrategy {

    String render(ReferencesItem item, SqlRenderer sqlRenderer, AstContext ctx);
}
