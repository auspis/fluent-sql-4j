package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lead;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.LeadRenderStrategy;

public class StandardSqlLeadRenderStrategy implements LeadRenderStrategy {
    @Override
    public String render(Lead lead, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("LEAD(")
                .append(lead.expression().accept(sqlRenderer, ctx))
                .append(", ")
                .append(lead.offset());

        if (lead.defaultValue() != null) {
            sql.append(", ").append(lead.defaultValue().accept(sqlRenderer, ctx));
        }

        sql.append(")");

        if (lead.overClause() != null) {
            sql.append(" ").append(lead.overClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
