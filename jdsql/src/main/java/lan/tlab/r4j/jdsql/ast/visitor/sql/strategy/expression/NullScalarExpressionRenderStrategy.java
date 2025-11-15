package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface NullScalarExpressionRenderStrategy extends ExpressionRenderStrategy {

    String render(Object expression, SqlRenderer sqlRenderer, AstContext ctx);
}
