package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class TableRenderStrategy implements SqlItemRenderStrategy {

    public String render(TableIdentifier table, SqlRenderer sqlRenderer, AstContext ctx) {
        String alias = table.alias().accept(sqlRenderer, ctx);
        if (!alias.isEmpty()) {
            alias = " " + alias;
        }
        return sqlRenderer.getEscapeStrategy().apply(table.name()) + alias;
    }
}
