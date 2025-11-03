package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface TableRenderStrategy extends SqlItemRenderStrategy {

    String render(TableIdentifier table, SqlRenderer sqlRenderer, AstContext ctx);
}
