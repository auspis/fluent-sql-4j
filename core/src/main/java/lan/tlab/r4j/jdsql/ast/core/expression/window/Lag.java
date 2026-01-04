package lan.tlab.r4j.jdsql.ast.core.expression.window;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Represents the LAG() window function. Provides access to a row at a given physical offset prior
 * to the current row within the partition.
 *
 * <p>Example: LAG(salary, 1) OVER (ORDER BY hire_date)
 *
 * @param expression the expression to evaluate
 * @param offset the number of rows back from the current row
 * @param defaultValue the default value to return when the offset goes beyond the window (can be null)
 * @param overClause the OVER clause specifying partitioning and ordering
 */
public record Lag(ScalarExpression expression, int offset, ScalarExpression defaultValue, OverClause overClause)
        implements WindowFunction {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
