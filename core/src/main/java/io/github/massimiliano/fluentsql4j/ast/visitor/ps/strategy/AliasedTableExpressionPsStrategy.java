package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.AliasedTableExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

/**
 * Strategy for rendering {@link AliasedTableExpression} in PreparedStatement context.
 *
 * @since 1.0
 */
public interface AliasedTableExpressionPsStrategy {

    /**
     * Handles rendering of an aliased table expression.
     *
     * @param item the aliased table expression to render
     * @param visitor the PreparedStatement visitor
     * @param ctx the AST context
     * @return the PreparedStatement DTO with SQL and parameters
     */
    PreparedStatementSpec handle(
            AliasedTableExpression item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx);
}
