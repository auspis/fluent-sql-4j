package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.expression.item.As;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class AsRenderStrategy implements SqlItemRenderStrategy {

    public String render(As as, SqlRenderer sqlRenderer, AstContext ctx) {
        String name = as.getName();
        return name.isBlank() ? "" : String.format("AS %s", name);
    }
}
