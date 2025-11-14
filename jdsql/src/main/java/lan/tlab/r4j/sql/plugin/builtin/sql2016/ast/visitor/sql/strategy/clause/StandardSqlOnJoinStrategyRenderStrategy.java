package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.OnJoinStrategyRenderStrategy;

public class StandardSqlOnJoinStrategyRenderStrategy implements OnJoinStrategyRenderStrategy {

    @Override
    public String render(OnJoin onJoin, SqlRenderer sqlRenderer, AstContext ctx) {
        if (onJoin.onCondition() == null) {
            return String.format(
                    "%s %s JOIN %s",
                    onJoin.left().accept(sqlRenderer, ctx),
                    onJoin.type().name(),
                    onJoin.right().accept(sqlRenderer, ctx));
        }
        return String.format(
                "%s %s JOIN %s ON %s",
                onJoin.left().accept(sqlRenderer, ctx),
                onJoin.type().name(),
                onJoin.right().accept(sqlRenderer, ctx),
                onJoin.onCondition().accept(sqlRenderer, ctx));
    }
}
