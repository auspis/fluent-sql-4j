package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.set.AliasedTableExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

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
