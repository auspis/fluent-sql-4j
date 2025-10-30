package lan.tlab.r4j.sql.ast.expression.scalar.call.window;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the LAG() window function. Provides access to a row at a given physical offset prior
 * to the current row within the partition.
 *
 * <p>Example: LAG(salary, 1) OVER (ORDER BY hire_date)
 */
public class Lag implements WindowFunction {

    private final ScalarExpression expression;
    private final int offset;
    private final ScalarExpression defaultValue;
    private OverClause overClause;

    public Lag(ScalarExpression expression, int offset) {
        this(expression, offset, null);
    }

    public Lag(ScalarExpression expression, int offset, ScalarExpression defaultValue) {
        this.expression = expression;
        this.offset = offset;
        this.defaultValue = defaultValue;
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    /**
     * Specifies the OVER clause for this window function.
     *
     * @param overClause the OVER clause
     * @return this Lag instance for method chaining
     */
    public Lag over(OverClause overClause) {
        this.overClause = overClause;
        return this;
    }

    public ScalarExpression getExpression() {
        return expression;
    }

    public int getOffset() {
        return offset;
    }

    public ScalarExpression getDefaultValue() {
        return defaultValue;
    }

    public OverClause getOverClause() {
        return overClause;
    }
}
