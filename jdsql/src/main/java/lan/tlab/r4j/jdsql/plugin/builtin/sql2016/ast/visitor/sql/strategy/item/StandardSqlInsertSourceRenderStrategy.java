package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertSource;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.InsertSourceRenderStrategy;

public class StandardSqlInsertSourceRenderStrategy implements InsertSourceRenderStrategy {
    @Override
    public String render(InsertSource item, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s", item.setExpression().accept(sqlRenderer, ctx));
    }
}
