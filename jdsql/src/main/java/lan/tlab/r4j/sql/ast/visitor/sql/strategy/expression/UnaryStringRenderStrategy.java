package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface UnaryStringRenderStrategy extends ExpressionRenderStrategy {

    String render(UnaryString functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
