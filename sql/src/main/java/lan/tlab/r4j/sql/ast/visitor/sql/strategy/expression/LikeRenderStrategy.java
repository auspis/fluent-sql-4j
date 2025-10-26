package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.Like;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class LikeRenderStrategy implements ExpressionRenderStrategy {

    public String render(Like expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s LIKE '%s'", expression.expression().accept(sqlRenderer, ctx), expression.pattern());
    }
}
