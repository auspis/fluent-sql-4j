package io.github.auspis.fluentsql4j.ast.core.expression.window;

import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

/**
 * Represents the RANK() window function. Assigns a rank to each row within a partition of a result
 * set. Rows with equal values receive the same rank, with gaps in the ranking sequence.
 *
 * <p>Example: RANK() OVER (PARTITION BY department ORDER BY salary DESC)
 *
 * @param overClause the OVER clause specifying partitioning and ordering
 */
public record Rank(OverClause overClause) implements WindowFunction {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
