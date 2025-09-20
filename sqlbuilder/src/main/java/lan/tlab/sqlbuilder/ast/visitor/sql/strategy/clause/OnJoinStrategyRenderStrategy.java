package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause;

import lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class OnJoinStrategyRenderStrategy implements ClauseRenderStrategy {

    public String render(OnJoin onJoin, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s %s JOIN %s ON %s",
                onJoin.getLeft().accept(sqlRenderer, ctx),
                onJoin.getType().name(),
                onJoin.getRight().accept(sqlRenderer, ctx),
                onJoin.getOnCondition().accept(sqlRenderer, ctx));
    }
}
