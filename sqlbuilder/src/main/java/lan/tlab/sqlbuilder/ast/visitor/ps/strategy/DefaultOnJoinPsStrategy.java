package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultOnJoinPsStrategy implements OnJoinPsStrategy {
    @Override
    public PsDto handle(OnJoin join, Visitor<PsDto> visitor, AstContext ctx) {
        // Visit left and right sources
        PsDto leftResult = join.getLeft().accept(visitor, ctx);
        PsDto rightResult = join.getRight().accept(visitor, ctx);
        String joinType;
        switch (join.getType()) {
            case INNER -> joinType = "INNER JOIN";
            case LEFT -> joinType = "LEFT JOIN";
            case RIGHT -> joinType = "RIGHT JOIN";
            case FULL -> joinType = "FULL JOIN";
            case CROSS -> joinType = "CROSS JOIN";
            default -> throw new UnsupportedOperationException("Unknown join type: " + join.getType());
        }
        StringBuilder sql = new StringBuilder();
        sql.append(leftResult.sql()).append(" ").append(joinType).append(" ").append(rightResult.sql());
        List<Object> params = new ArrayList<>();
        params.addAll(leftResult.parameters());
        params.addAll(rightResult.parameters());
        // ON condition (not for CROSS JOIN)
        if (join.getType() != OnJoin.JoinType.CROSS) {
            if (join.getOnCondition() != null) {
                // Passa il contesto con scope JOIN_ON
                PsDto onResult = join.getOnCondition().accept(visitor, new AstContext(AstContext.Scope.JOIN_ON));
                sql.append(" ON ").append(onResult.sql());
                params.addAll(onResult.parameters());
            }
        }
        return new PsDto(sql.toString(), params);
    }
}
