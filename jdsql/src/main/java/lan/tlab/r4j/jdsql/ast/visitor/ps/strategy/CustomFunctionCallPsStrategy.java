package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

/**
 * Strategy interface for rendering custom function calls in prepared statements.
 * <p>
 * Implementations of this interface provide dialect-specific rendering logic
 * for {@link CustomFunctionCall} nodes in prepared statements, including handling of function options
 * such as SEPARATOR in GROUP_CONCAT.
 *
 * @see CustomFunctionCall
 * @see lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer
 */
public interface CustomFunctionCallPsStrategy {
    PsDto handle(CustomFunctionCall functionCall, PreparedStatementRenderer renderer, AstContext ctx);
}
