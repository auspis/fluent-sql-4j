package io.github.auspis.fluentsql4j.ast.core.predicate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NullPredicateTest {

    @Test
    void nullPredicateConstruction() {
        NullPredicate nullPred = new NullPredicate();

        assertThat(nullPred).isNotNull();
    }

    @Test
    void nullPredicateIsInstanceOfPredicate() {
        NullPredicate nullPred = new NullPredicate();

        assertThat(nullPred).isInstanceOf(Predicate.class);
    }

    @Test
    void nullPredicateInAndOrCombination() {
        NullPredicate null1 = new NullPredicate();
        NullPredicate null2 = new NullPredicate();
        Predicate andPred = AndOr.and(null1, null2);

        assertThat(andPred).isNotNull();
    }

    @Test
    void nullPredicateInOrCombination() {
        NullPredicate null1 = new NullPredicate();
        NullPredicate null2 = new NullPredicate();
        Predicate orPred = AndOr.or(null1, null2);

        assertThat(orPred).isNotNull();
    }
}
