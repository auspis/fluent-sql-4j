package io.github.auspis.fluentsql4j.ast.dql.clause;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FetchTest {

    @Test
    void nullOffsetNormalizesToZero() {
        Fetch fetch = new Fetch(null, 10L);

        assertThat(fetch.offset()).isZero();
        assertThat(fetch.rows()).isEqualTo(10L);
    }

    @Test
    void explicitOffsetIsPreserved() {
        Fetch fetch = new Fetch(5L, 20L);

        assertThat(fetch.offset()).isEqualTo(5L);
        assertThat(fetch.rows()).isEqualTo(20L);
    }

    @Test
    void isActiveReturnsTrueWhenRowsIsSet() {
        Fetch fetch = Fetch.of(10L);

        assertThat(fetch.isActive()).isTrue();
    }

    @Test
    void isActiveReturnsFalseWhenRowsIsNull() {
        Fetch fetch = Fetch.nullObject();

        assertThat(fetch.isActive()).isFalse();
    }

    @Test
    void ofFactoryCreatesWithZeroOffsetAndGivenRows() {
        Fetch fetch = Fetch.of(50L);

        assertThat(fetch.offset()).isZero();
        assertThat(fetch.rows()).isEqualTo(50L);
    }

    @Test
    void ofFactoryWithNullRowsIsInactive() {
        Fetch fetch = Fetch.of(null);

        assertThat(fetch.isActive()).isFalse();
        assertThat(fetch.rows()).isNull();
    }

    @Test
    void nullObjectHasZeroOffsetAndNullRows() {
        Fetch fetch = Fetch.nullObject();

        assertThat(fetch.offset()).isZero();
        assertThat(fetch.rows()).isNull();
    }
}
