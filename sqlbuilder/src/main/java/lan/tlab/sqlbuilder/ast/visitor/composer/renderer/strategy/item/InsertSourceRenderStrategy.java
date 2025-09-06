package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertSource;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class InsertSourceRenderStrategy implements SqlItemRenderStrategy {

    public String render(InsertSource item, SqlRenderer sqlRenderer) {
        return String.format("%s", item.getSetExpression().accept(sqlRenderer));
    }
}
