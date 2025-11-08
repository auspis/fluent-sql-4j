package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.predicate.Like;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.LikeRenderStrategy;

public class StandardSqlLikeRenderStrategy implements LikeRenderStrategy {

    @Override
    public String render(Like expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s LIKE '%s'", expression.expression().accept(sqlRenderer, ctx), expression.pattern());
    }
}
