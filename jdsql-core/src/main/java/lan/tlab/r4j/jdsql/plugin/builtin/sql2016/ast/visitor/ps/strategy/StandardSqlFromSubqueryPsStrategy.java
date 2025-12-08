package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.FromSubqueryPsStrategy;

public class StandardSqlFromSubqueryPsStrategy implements FromSubqueryPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            FromSubquery fromSubquery, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec subqueryResult = fromSubquery.getSubquery().accept(renderer, ctx);

        String sql = "(" + subqueryResult.sql() + ")";

        // Add alias if present (not empty name)
        if (!fromSubquery.getAs().name().isEmpty()) {
            PreparedStatementSpec aliasResult = fromSubquery.getAs().accept(renderer, ctx);
            sql += " AS " + aliasResult.sql();
        }

        return new PreparedStatementSpec(sql, subqueryResult.parameters());
    }
}
