package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.window.Rank;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.RankPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlRankPsStrategy implements RankPsStrategy {

    @Override
    public PreparedStatementSpec handle(Rank rank, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("RANK()");
        List<Object> parameters = new ArrayList<>();

        if (rank.overClause() != null) {
            PreparedStatementSpec overResult = rank.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
