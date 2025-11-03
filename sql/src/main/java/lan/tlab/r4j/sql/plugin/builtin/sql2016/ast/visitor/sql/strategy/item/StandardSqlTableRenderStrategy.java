package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.TableRenderStrategy;

public class StandardSqlTableRenderStrategy implements TableRenderStrategy {

    @Override
    public String render(TableIdentifier table, SqlRenderer sqlRenderer, AstContext ctx) {
        String alias = table.alias().accept(sqlRenderer, ctx);
        if (!alias.isEmpty()) {
            alias = " " + alias;
        }
        return sqlRenderer.getEscapeStrategy().apply(table.name()) + alias;
    }
}
