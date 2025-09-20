package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertSource;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class InsertSourceRenderStrategy implements SqlItemRenderStrategy {

    public String render(InsertSource item, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s", item.getSetExpression().accept(sqlRenderer, ctx));
    }
}
