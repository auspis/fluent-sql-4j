package io.github.massimiliano.fluentsql4j.ast.core.expression.window;

import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

/**
 * Represents the DENSE_RANK() window function. Assigns a rank to each row within a partition of a
 * result set. Rows with equal values receive the same rank, without gaps in the ranking sequence.
 *
 * <p>Example: DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC)
 *
 * @param overClause the OVER clause specifying partitioning and ordering
 */
public record DenseRank(OverClause overClause) implements WindowFunction {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
