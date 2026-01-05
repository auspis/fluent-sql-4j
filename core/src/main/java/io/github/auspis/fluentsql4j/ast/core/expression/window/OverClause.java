package io.github.auspis.fluentsql4j.ast.core.expression.window;

import java.util.List;
import java.util.stream.Stream;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.dql.clause.Sorting;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

/**
 * Represents the OVER clause used with window functions. The OVER clause defines the window
 * (partition and ordering) over which the window function operates.
 *
 * <p>Examples:
 *
 * <ul>
 *   <li>OVER (ORDER BY salary DESC)
 *   <li>OVER (PARTITION BY department ORDER BY salary DESC)
 *   <li>OVER (PARTITION BY department, location ORDER BY hire_date)
 * </ul>
 *
 * @param partitionBy optional list of expressions to partition the result set
 * @param orderBy optional list of sorting specifications for ordering within each partition
 */
public record OverClause(List<ScalarExpression> partitionBy, List<Sorting> orderBy) {

    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    /**
     * Builder for creating OverClause instances.
     */
    public static class Builder {
        private List<ScalarExpression> partitionBy;
        private List<Sorting> orderBy;

        public Builder partitionBy(ScalarExpression... partitionBy) {
            return partitionBy(Stream.of(partitionBy).toList());
        }

        public Builder partitionBy(List<ScalarExpression> partitionBy) {
            this.partitionBy = partitionBy;
            return this;
        }

        public Builder orderBy(Sorting... orderBy) {
            return orderBy(Stream.of(orderBy).toList());
        }

        public Builder orderBy(List<Sorting> orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public OverClause build() {
            return new OverClause(partitionBy, orderBy);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
