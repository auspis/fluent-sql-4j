package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class DefaultValuesRenderStrategy implements SqlItemRenderStrategy {

    public String render(DefaultValues item, SqlRenderer sqlRenderer) {
        return "DEFAULT VALUES";
    }
}
