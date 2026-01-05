package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.window.Ntile;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NtilePsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlNtilePsStrategy implements NtilePsStrategy {

    @Override
    public PreparedStatementSpec handle(Ntile ntile, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("NTILE(").append(ntile.buckets()).append(")");
        List<Object> parameters = new ArrayList<>();

        if (ntile.overClause() != null) {
            PreparedStatementSpec overResult = ntile.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
