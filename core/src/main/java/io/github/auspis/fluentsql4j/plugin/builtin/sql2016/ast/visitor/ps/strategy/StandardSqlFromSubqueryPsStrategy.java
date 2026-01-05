package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dql.source.FromSubquery;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.FromSubqueryPsStrategy;

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
