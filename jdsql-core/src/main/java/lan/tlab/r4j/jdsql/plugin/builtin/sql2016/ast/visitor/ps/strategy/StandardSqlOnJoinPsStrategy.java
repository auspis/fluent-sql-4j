package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.OnJoinPsStrategy;

public class StandardSqlOnJoinPsStrategy implements OnJoinPsStrategy {
    @Override
    public PsDto handle(OnJoin join, Visitor<PsDto> renderer, AstContext ctx) {
        // Visit left and right sources
        PsDto leftResult = join.left().accept(renderer, ctx);
        PsDto rightResult = join.right().accept(renderer, ctx);
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
                PsDto onResult = join.onCondition().accept(renderer, ctx);
                sql.append(" ON ").append(onResult.sql());
                params.addAll(onResult.parameters());
            }
        }
        return new PsDto(sql.toString(), params);
    }
}
