package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Concat;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface ConcatRenderStrategy extends ExpressionRenderStrategy {

    public String render(Concat functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
