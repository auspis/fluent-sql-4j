package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class AsRenderStrategy implements SqlItemRenderStrategy {

    public String render(As as, SqlRenderer sqlRenderer) {
        String name = as.getName();
        return name.isBlank() ? "" : String.format("AS %s", name);
    }
}
