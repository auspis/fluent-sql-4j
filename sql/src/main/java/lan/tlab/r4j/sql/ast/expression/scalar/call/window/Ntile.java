package lan.tlab.r4j.sql.ast.expression.scalar.call.window;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the NTILE() window function. Distributes the rows in an ordered partition into a
 * specified number of approximately equal groups, or buckets.
 *
 * <p>Example: NTILE(4) OVER (ORDER BY salary DESC)
 */
public class Ntile implements WindowFunction {

    private final int buckets;
    private OverClause overClause;

    public Ntile(int buckets) {
        this.buckets = buckets;
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    /**
     * Specifies the OVER clause for this window function.
     *
     * @param overClause the OVER clause
     * @return this Ntile instance for method chaining
     */
    public Ntile over(OverClause overClause) {
        this.overClause = overClause;
        return this;
    }

    public int getBuckets() {
        return buckets;
    }

    public OverClause getOverClause() {
        return overClause;
    }
}
