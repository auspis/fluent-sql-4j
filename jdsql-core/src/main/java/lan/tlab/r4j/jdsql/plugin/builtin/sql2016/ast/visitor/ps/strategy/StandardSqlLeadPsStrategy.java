package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lead;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LeadPsStrategy;

public class StandardSqlLeadPsStrategy implements LeadPsStrategy {

    @Override
    public PreparedStatementSpec handle(Lead lead, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        List<Object> parameters = new ArrayList<>();
        PreparedStatementSpec exprResult = lead.expression().accept(visitor, ctx);
        parameters.addAll(exprResult.parameters());

        StringBuilder sql =
                new StringBuilder("LEAD(").append(exprResult.sql()).append(", ").append(lead.offset());

        if (lead.defaultValue() != null) {
            PreparedStatementSpec defaultResult = lead.defaultValue().accept(visitor, ctx);
            sql.append(", ").append(defaultResult.sql());
            parameters.addAll(defaultResult.parameters());
        }

        sql.append(")");

        if (lead.overClause() != null) {
            PreparedStatementSpec overResult = lead.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
