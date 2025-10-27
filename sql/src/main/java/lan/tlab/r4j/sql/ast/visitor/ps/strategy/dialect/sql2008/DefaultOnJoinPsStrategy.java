package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.OnJoinPsStrategy;

public class DefaultOnJoinPsStrategy implements OnJoinPsStrategy {
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
                // Passa il contesto con scope JOIN_ON
                PsDto onResult = join.onCondition().accept(renderer, new AstContext(AstContext.Scope.JOIN_ON));
                sql.append(" ON ").append(onResult.sql());
                params.addAll(onResult.parameters());
            }
        }
        return new PsDto(sql.toString(), params);
    }
}
