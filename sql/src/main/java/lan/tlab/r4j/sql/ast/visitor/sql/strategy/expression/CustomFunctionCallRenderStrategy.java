package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

/**
 * Strategy interface for rendering custom function calls in SQL.
 * <p>
 * Implementations of this interface provide dialect-specific rendering logic
 * for {@link CustomFunctionCall} nodes, including handling of function options
 * such as SEPARATOR in GROUP_CONCAT.
 *
 * @see CustomFunctionCall
 * @see lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer
 */
public interface CustomFunctionCallRenderStrategy extends ExpressionRenderStrategy {

    String render(CustomFunctionCall functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
