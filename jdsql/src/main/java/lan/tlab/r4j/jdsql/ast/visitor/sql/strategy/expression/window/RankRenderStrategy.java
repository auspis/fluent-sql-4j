package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Rank;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public interface RankRenderStrategy extends ExpressionRenderStrategy {

    String render(Rank rank, SqlRenderer sqlRenderer, AstContext ctx);
}
