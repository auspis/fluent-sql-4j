package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.DefaultValues;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.DefaultValuesRenderStrategy;

public class StandardSqlDefaultValuesRenderStrategy implements DefaultValuesRenderStrategy {
    @Override
    public String render(DefaultValues item, SqlRenderer sqlRenderer, AstContext ctx) {
        return "DEFAULT VALUES";
    }
}
