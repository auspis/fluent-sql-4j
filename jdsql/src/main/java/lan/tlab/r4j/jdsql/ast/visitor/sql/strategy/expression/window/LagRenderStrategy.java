package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lag;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public interface LagRenderStrategy extends ExpressionRenderStrategy {

    String render(Lag lag, SqlRenderer sqlRenderer, AstContext ctx);
}
