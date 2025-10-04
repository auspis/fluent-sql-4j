package lan.tlab.r4j.sql.dsl.select;

import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.predicate.logical.AndOr;

/**
 * Represents how to combine boolean expressions in WHERE clauses.
 * This provides a type-safe alternative to boolean flags.
 */
public enum LogicalCombinator {
    AND {
        @Override
        public Predicate combine(Predicate left, Predicate right) {
            return AndOr.and(left, right);
        }
    },
    OR {
        @Override
        public Predicate combine(Predicate left, Predicate right) {
            return AndOr.or(left, right);
        }
    };

    public abstract Predicate combine(Predicate left, Predicate right);
}
