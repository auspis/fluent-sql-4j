package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertSource;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class InsertSourceRenderStrategy implements SqlItemRenderStrategy {

    public String render(InsertSource item, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s", item.getSetExpression().accept(sqlRenderer, ctx));
    }
}
