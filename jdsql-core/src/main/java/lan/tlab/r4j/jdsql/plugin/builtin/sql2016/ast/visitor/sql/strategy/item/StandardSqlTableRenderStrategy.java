package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.TableRenderStrategy;

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
