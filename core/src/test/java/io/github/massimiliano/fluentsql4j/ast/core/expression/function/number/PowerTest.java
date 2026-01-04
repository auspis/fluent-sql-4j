package io.github.massimiliano.fluentsql4j.ast.core.expression.function.number;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import org.junit.jupiter.api.Test;

class PowerTest {

    @Test
    void createsWithNumbersOnly() {
        Power power = Power.of(2, 3);

        assertThat(power.base()).isInstanceOf(Literal.class);
        assertThat(power.exponent()).isInstanceOf(Literal.class);
    }

    @Test
    void createsWithScalarBaseAndNumberExponent() {
        ColumnReference base = ColumnReference.of("table", "column");
        Power power = Power.of(base, 5);

        assertThat(power.base()).isEqualTo(base);
        assertThat(power.exponent()).isInstanceOf(Literal.class);
    }

    @Test
    void createsWithNumberBaseAndScalarExponent() {
        ColumnReference exponent = ColumnReference.of("table", "exp");
        Power power = Power.of(10, exponent);

        assertThat(power.base()).isInstanceOf(Literal.class);
        assertThat(power.exponent()).isEqualTo(exponent);
    }

    @Test
    void createsWithScalarBaseAndScalarExponent() {
        ColumnReference base = ColumnReference.of("t1", "base");
        ColumnReference exponent = ColumnReference.of("t2", "exp");
        Power power = Power.of(base, exponent);

        assertThat(power.base()).isEqualTo(base);
        assertThat(power.exponent()).isEqualTo(exponent);
    }

    @Test
    void powerWithPositiveIntegerBase() {
        Power power = Power.of(2, 10);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithNegativeBase() {
        Power power = Power.of(-2, 3);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithDecimalBase() {
        Power power = Power.of(2.5, 2);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithZeroExponent() {
        Power power = Power.of(5, 0);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithNegativeExponent() {
        Power power = Power.of(2, -3);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithDecimalExponent() {
        Power power = Power.of(4, 0.5);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithBaseOne() {
        Power power = Power.of(1, 1000);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithLargeExponent() {
        Power power = Power.of(2, 100);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithSmallBase() {
        Power power = Power.of(0.1, 5);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }

    @Test
    void powerWithBothNegative() {
        Power power = Power.of(-3, -2);

        assertThat(power.base()).isNotNull();
        assertThat(power.exponent()).isNotNull();
    }
}
