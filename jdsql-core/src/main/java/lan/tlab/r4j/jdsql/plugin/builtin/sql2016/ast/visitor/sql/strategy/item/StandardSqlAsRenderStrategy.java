package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.common.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.AsRenderStrategy;

public class StandardSqlAsRenderStrategy implements AsRenderStrategy {

    @Override
    public String render(Alias as, SqlRenderer sqlRenderer, AstContext ctx) {
        String name = as.name();
        return name.isBlank() ? "" : String.format("AS %s", name);
    }
}
