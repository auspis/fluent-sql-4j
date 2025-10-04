package lan.tlab.sqlbuilder.dsl.select;

import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;

/**
 * Represents how to combine boolean expressions in WHERE clauses.
 * This provides a type-safe alternative to boolean flags.
 */
public enum LogicalCombinator {
    AND {
        @Override
        public BooleanExpression combine(BooleanExpression left, BooleanExpression right) {
            return AndOr.and(left, right);
        }
    },
    OR {
        @Override
        public BooleanExpression combine(BooleanExpression left, BooleanExpression right) {
            return AndOr.or(left, right);
        }
    };

    public abstract BooleanExpression combine(BooleanExpression left, BooleanExpression right);
}
