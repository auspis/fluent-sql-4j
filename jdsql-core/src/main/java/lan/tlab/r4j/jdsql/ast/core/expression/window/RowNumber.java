package lan.tlab.r4j.jdsql.ast.core.expression.window;

import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Represents the ROW_NUMBER() window function. Assigns a unique sequential integer to each row
 * within a partition of a result set, starting at 1 for the first row in each partition.
 *
 * <p>Example: ROW_NUMBER() OVER (ORDER BY salary DESC)
 *
 * @param overClause the OVER clause specifying partitioning and ordering
 */
public record RowNumber(OverClause overClause) implements WindowFunction {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
