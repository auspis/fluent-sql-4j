package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.OverClause;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public interface OverClauseRenderStrategy extends ExpressionRenderStrategy {

    String render(OverClause overClause, SqlRenderer sqlRenderer, AstContext ctx);
}
