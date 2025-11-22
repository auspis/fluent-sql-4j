package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface TableRenderStrategy extends SqlItemRenderStrategy {

    String render(TableIdentifier table, SqlRenderer sqlRenderer, AstContext ctx);
}
