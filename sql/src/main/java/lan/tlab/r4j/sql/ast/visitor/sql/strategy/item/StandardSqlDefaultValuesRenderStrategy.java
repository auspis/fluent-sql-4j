package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class StandardSqlDefaultValuesRenderStrategy implements SqlItemRenderStrategy {

    public String render(DefaultValues item, SqlRenderer sqlRenderer, AstContext ctx) {
        return "DEFAULT VALUES";
    }
}
