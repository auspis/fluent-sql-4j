package lan.tlab.r4j.sql.ast.common.expression.scalar.window;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the NTILE() window function. Distributes the rows in an ordered partition into a
 * specified number of approximately equal groups, or buckets.
 *
 * <p>Example: NTILE(4) OVER (ORDER BY salary DESC)
 *
 * @param buckets the number of buckets to divide the rows into
 * @param overClause the OVER clause specifying partitioning and ordering
 */
public record Ntile(int buckets, OverClause overClause) implements WindowFunction {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
