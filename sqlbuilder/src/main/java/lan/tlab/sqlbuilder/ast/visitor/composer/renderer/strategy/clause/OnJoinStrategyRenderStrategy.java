package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class OnJoinStrategyRenderStrategy implements ClauseRenderStrategy {

    public String render(OnJoin onJoin, SqlRenderer sqlRenderer) {
        return String.format(
                "%s %s JOIN %s ON %s",
                onJoin.getLeft().accept(sqlRenderer),
                onJoin.getType().name(),
                onJoin.getRight().accept(sqlRenderer),
                onJoin.getOnCondition().accept(sqlRenderer));
    }
}
