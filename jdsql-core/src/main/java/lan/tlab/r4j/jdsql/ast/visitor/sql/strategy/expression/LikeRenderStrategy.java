package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.predicate.Like;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface LikeRenderStrategy extends ExpressionRenderStrategy {

    String render(Like expression, SqlRenderer sqlRenderer, AstContext ctx);
}
