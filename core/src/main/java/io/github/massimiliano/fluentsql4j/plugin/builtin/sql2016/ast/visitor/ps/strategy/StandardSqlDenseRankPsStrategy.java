package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.window.DenseRank;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.DenseRankPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlDenseRankPsStrategy implements DenseRankPsStrategy {

    @Override
    public PreparedStatementSpec handle(DenseRank denseRank, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("DENSE_RANK()");
        List<Object> parameters = new ArrayList<>();

        if (denseRank.overClause() != null) {
            PreparedStatementSpec overResult = denseRank.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
