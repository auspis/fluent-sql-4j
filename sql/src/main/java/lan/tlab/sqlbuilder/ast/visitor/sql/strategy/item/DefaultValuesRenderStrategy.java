package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class DefaultValuesRenderStrategy implements SqlItemRenderStrategy {

    public String render(DefaultValues item, SqlRenderer sqlRenderer, AstContext ctx) {
        return "DEFAULT VALUES";
    }
}
