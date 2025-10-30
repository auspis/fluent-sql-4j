package lan.tlab.r4j.sql.ast.expression.scalar.call.window;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the LEAD() window function. Provides access to a row at a given physical offset after
 * the current row within the partition.
 *
 * <p>Example: LEAD(salary, 1) OVER (ORDER BY hire_date)
 *
 * @param expression the expression to evaluate
 * @param offset the number of rows forward from the current row
 * @param defaultValue the default value to return when the offset goes beyond the window (can be null)
 * @param overClause the OVER clause specifying partitioning and ordering
 */
public record Lead(ScalarExpression expression, int offset, ScalarExpression defaultValue, OverClause overClause)
        implements WindowFunction {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
