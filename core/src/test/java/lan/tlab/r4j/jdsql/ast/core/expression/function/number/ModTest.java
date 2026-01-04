package lan.tlab.r4j.jdsql.ast.core.expression.function.number;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import org.junit.jupiter.api.Test;

class ModTest {

    @Test
    void createsWithNumbersOnly() {
        Mod mod = Mod.of(10, 3);

        assertThat(mod.dividend()).isInstanceOf(Literal.class);
        assertThat(mod.divisor()).isInstanceOf(Literal.class);
    }

    @Test
    void createsWithScalarDividendAndNumberDivisor() {
        ColumnReference dividend = ColumnReference.of("table", "value");
        Mod mod = Mod.of(dividend, 5);

        assertThat(mod.dividend()).isEqualTo(dividend);
        assertThat(mod.divisor()).isInstanceOf(Literal.class);
    }

    @Test
    void createsWithNumberDividendAndScalarDivisor() {
        ColumnReference divisor = ColumnReference.of("table", "div");
        Mod mod = Mod.of(100, divisor);

        assertThat(mod.dividend()).isInstanceOf(Literal.class);
        assertThat(mod.divisor()).isEqualTo(divisor);
    }

    @Test
    void createsWithBothScalarExpressions() {
        ColumnReference dividend = ColumnReference.of("t1", "val");
        ColumnReference divisor = ColumnReference.of("t2", "div");
        Mod mod = new Mod(dividend, divisor);

        assertThat(mod.dividend()).isEqualTo(dividend);
        assertThat(mod.divisor()).isEqualTo(divisor);
    }

    @Test
    void modWithPositiveNumbers() {
        Mod mod = Mod.of(17, 5);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modWithNegativeDividend() {
        Mod mod = Mod.of(-17, 5);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modWithNegativeDivisor() {
        Mod mod = Mod.of(17, -5);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modWithBothNegative() {
        Mod mod = Mod.of(-17, -5);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modWithZeroDividend() {
        Mod mod = Mod.of(0, 5);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modWithDividendSmallerThanDivisor() {
        Mod mod = Mod.of(3, 10);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modWithDecimalNumbers() {
        Mod mod = Mod.of(10.5, 3.2);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modWithLargeDividend() {
        Mod mod = Mod.of(1000000, 7);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modWithOne() {
        Mod mod = Mod.of(100, 1);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }

    @Test
    void modEqualNumbers() {
        Mod mod = Mod.of(5, 5);

        assertThat(mod.dividend()).isNotNull();
        assertThat(mod.divisor()).isNotNull();
    }
}
