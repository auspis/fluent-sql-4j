package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.MergeUsing;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class MergeUsingRenderStrategy {

    public String render(MergeUsing using, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder();

        // Check if source is a subquery (SelectStatement)
        if (using.getSource() instanceof SelectStatement) {
            sql.append("(").append(using.getSource().accept(sqlRenderer, ctx)).append(")");
        } else {
            sql.append(using.getSource().accept(sqlRenderer, ctx));
        }

        String sourceAlias = using.getSourceAlias().accept(sqlRenderer, ctx);
        if (!sourceAlias.isEmpty()) {
            sql.append(" ").append(sourceAlias);
        }

        return sql.toString();
    }
}
