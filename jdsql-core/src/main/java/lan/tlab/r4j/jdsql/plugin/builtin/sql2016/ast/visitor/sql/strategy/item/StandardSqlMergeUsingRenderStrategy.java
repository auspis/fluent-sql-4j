package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.MergeUsing;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.MergeUsingRenderStrategy;

public class StandardSqlMergeUsingRenderStrategy implements MergeUsingRenderStrategy {
    @Override
    public String render(MergeUsing using, SqlRenderer sqlRenderer, AstContext ctx) {
        return using.source().accept(sqlRenderer, ctx);
    }
}
