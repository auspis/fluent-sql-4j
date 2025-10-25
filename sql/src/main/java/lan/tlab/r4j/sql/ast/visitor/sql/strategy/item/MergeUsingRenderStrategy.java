package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.MergeUsing;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class MergeUsingRenderStrategy {

    public String render(MergeUsing using, SqlRenderer sqlRenderer, AstContext ctx) {
        // Delegate to the TableExpression's rendering
        return using.source().accept(sqlRenderer, ctx);
    }
}
