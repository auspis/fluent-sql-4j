package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface UnaryNumericRenderStrategy extends ExpressionRenderStrategy {

    String render(UnaryNumeric functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
