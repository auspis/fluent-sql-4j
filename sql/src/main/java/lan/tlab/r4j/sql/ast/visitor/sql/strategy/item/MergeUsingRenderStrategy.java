package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.MergeUsing;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class MergeUsingRenderStrategy {

    public String render(MergeUsing using, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder();
        sql.append(using.getSource().accept(sqlRenderer, ctx));

        if (!using.getSourceAlias().getName().isEmpty()) {
            sql.append(" AS ").append(using.getSourceAlias().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
