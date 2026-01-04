package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

/**
 * Strategy interface for rendering custom function calls in prepared statements.
 * <p>
 * Implementations of this interface provide dialect-specific rendering logic
 * for {@link CustomFunctionCall} nodes in prepared statements, including handling of function options
 * such as SEPARATOR in GROUP_CONCAT.
 *
 * @see CustomFunctionCall
 * @see io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor
 */
public interface CustomFunctionCallPsStrategy {
    PreparedStatementSpec handle(
            CustomFunctionCall functionCall, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
