package lan.tlab.r4j.sql.ast.expression.scalar.call.window;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the ROW_NUMBER() window function. Assigns a unique sequential integer to each row
 * within a partition of a result set, starting at 1 for the first row in each partition.
 *
 * <p>Example: ROW_NUMBER() OVER (ORDER BY salary DESC)
 */
public class RowNumber implements WindowFunction {

    private OverClause overClause;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    /**
     * Specifies the OVER clause for this window function.
     *
     * @param overClause the OVER clause
     * @return this RowNumber instance for method chaining
     */
    public RowNumber over(OverClause overClause) {
        this.overClause = overClause;
        return this;
    }

    public OverClause getOverClause() {
        return overClause;
    }
}
