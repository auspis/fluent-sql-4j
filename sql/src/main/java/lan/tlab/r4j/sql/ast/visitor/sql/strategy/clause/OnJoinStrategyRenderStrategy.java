package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class OnJoinStrategyRenderStrategy implements ClauseRenderStrategy {

    public String render(OnJoin onJoin, SqlRenderer sqlRenderer, AstContext ctx) {
        if (onJoin.getOnCondition() == null) {
            return String.format(
                    "%s %s JOIN %s",
                    onJoin.getLeft().accept(sqlRenderer, ctx),
                    onJoin.getType().name(),
                    onJoin.getRight().accept(sqlRenderer, ctx));
        }
        return String.format(
                "%s %s JOIN %s ON %s",
                onJoin.getLeft().accept(sqlRenderer, ctx),
                onJoin.getType().name(),
                onJoin.getRight().accept(sqlRenderer, ctx),
                onJoin.getOnCondition().accept(sqlRenderer, ctx));
    }
}
