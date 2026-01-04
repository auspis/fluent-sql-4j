package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.source.join.OnJoin;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.OnJoinPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlOnJoinPsStrategy implements OnJoinPsStrategy {
    @Override
    public PreparedStatementSpec handle(OnJoin join, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        // Visit left and right sources
        PreparedStatementSpec leftResult = join.left().accept(renderer, ctx);
        PreparedStatementSpec rightResult = join.right().accept(renderer, ctx);
        String joinType;
        switch (join.type()) {
            case INNER -> joinType = "INNER JOIN";
            case LEFT -> joinType = "LEFT JOIN";
            case RIGHT -> joinType = "RIGHT JOIN";
            case FULL -> joinType = "FULL JOIN";
            case CROSS -> joinType = "CROSS JOIN";
            default -> throw new UnsupportedOperationException("Unknown join type: " + join.type());
        }
        StringBuilder sql = new StringBuilder();
        sql.append(leftResult.sql()).append(" ").append(joinType).append(" ").append(rightResult.sql());
        List<Object> params = new ArrayList<>();
        params.addAll(leftResult.parameters());
        params.addAll(rightResult.parameters());
        // ON condition (not for CROSS JOIN)
        if (join.type() != OnJoin.JoinType.CROSS) {
            if (join.onCondition() != null) {
                // Use propagated context (already enriched with JOIN_ON by ContextPreparationVisitor)
                PreparedStatementSpec onResult = join.onCondition().accept(renderer, ctx);
                sql.append(" ON ").append(onResult.sql());
                params.addAll(onResult.parameters());
            }
        }
        return new PreparedStatementSpec(sql.toString(), params);
    }
}
