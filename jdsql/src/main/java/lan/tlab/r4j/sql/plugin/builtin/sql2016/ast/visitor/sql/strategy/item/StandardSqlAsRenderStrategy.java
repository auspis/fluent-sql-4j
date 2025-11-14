package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.common.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.AsRenderStrategy;

public class StandardSqlAsRenderStrategy implements AsRenderStrategy {

    @Override
    public String render(Alias as, SqlRenderer sqlRenderer, AstContext ctx) {
        String name = as.name();
        return name.isBlank() ? "" : String.format("AS %s", name);
    }
}
