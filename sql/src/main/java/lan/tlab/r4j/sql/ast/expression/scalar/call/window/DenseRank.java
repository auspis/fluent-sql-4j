package lan.tlab.r4j.sql.ast.expression.scalar.call.window;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the DENSE_RANK() window function. Assigns a rank to each row within a partition of a
 * result set. Rows with equal values receive the same rank, without gaps in the ranking sequence.
 *
 * <p>Example: DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC)
 */
public class DenseRank implements WindowFunction {

    private OverClause overClause;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    /**
     * Specifies the OVER clause for this window function.
     *
     * @param overClause the OVER clause
     * @return this DenseRank instance for method chaining
     */
    public DenseRank over(OverClause overClause) {
        this.overClause = overClause;
        return this;
    }

    public OverClause getOverClause() {
        return overClause;
    }
}
