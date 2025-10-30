package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Lead;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public class LeadRenderStrategy implements ExpressionRenderStrategy {

    public String render(Lead lead, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("LEAD(")
                .append(lead.getExpression().accept(sqlRenderer, ctx))
                .append(", ")
                .append(lead.getOffset());

        if (lead.getDefaultValue() != null) {
            sql.append(", ").append(lead.getDefaultValue().accept(sqlRenderer, ctx));
        }

        sql.append(")");

        if (lead.getOverClause() != null) {
            sql.append(" ").append(lead.getOverClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
