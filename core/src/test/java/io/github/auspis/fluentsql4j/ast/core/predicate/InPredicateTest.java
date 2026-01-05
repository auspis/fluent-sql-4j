package io.github.auspis.fluentsql4j.ast.core.predicate;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.ValueExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class InPredicateTest {

    @Test
    void inPredicateWithVarargs() {
        In in = new In(Literal.of("col"), Literal.of("a"), Literal.of("b"), Literal.of("c"));

        assertThat(in).isNotNull();
    }

    @Test
    void inPredicateWithList() {
        List<ValueExpression> values = new ArrayList<>();
        values.add(Literal.of("x"));
        values.add(Literal.of("y"));
        values.add(Literal.of("z"));

        In in = new In(Literal.of("col"), values);

        assertThat(in).isNotNull();
    }

    @Test
    void inPredicateWithMixedTypes() {
        List<ValueExpression> values = new ArrayList<>();
        values.add(Literal.of("string"));
        values.add(Literal.of(42));
        values.add(Literal.of(3.14));

        In in = new In(Literal.of("col"), values);

        assertThat(in).isNotNull();
    }

    @Test
    void inPredicateWithSingleValue() {
        In in = new In(Literal.of("col"), Literal.of("single"));

        assertThat(in).isNotNull();
    }

    @Test
    void inPredicateWithLargeList() {
        List<ValueExpression> values = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            values.add(Literal.of(i));
        }

        In in = new In(Literal.of("col"), values);

        assertThat(in).isNotNull();
    }

    @Test
    void inPredicateIsInstanceOfPredicate() {
        In in = new In(Literal.of("col"), Literal.of("a"), Literal.of("b"));

        assertThat(in).isInstanceOf(Predicate.class);
    }
}
