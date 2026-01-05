package io.github.auspis.fluentsql4j.dsl.clause;

import io.github.auspis.fluentsql4j.ast.core.predicate.AndOr;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;

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
