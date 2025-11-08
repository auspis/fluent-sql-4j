package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.common.expression.scalar.window.Ntile;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public interface NtileRenderStrategy extends ExpressionRenderStrategy {

    String render(Ntile ntile, SqlRenderer sqlRenderer, AstContext ctx);
}
