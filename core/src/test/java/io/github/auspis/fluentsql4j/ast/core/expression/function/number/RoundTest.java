package io.github.auspis.fluentsql4j.ast.core.expression.function.number;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import org.junit.jupiter.api.Test;

class RoundTest {

    @Test
    void createsRoundWithNumberAndDecimalPlaces() {
        Round round = Round.of(3.14159, 2);

        assertThat(round.numericExpression()).isInstanceOf(Literal.class);
        assertThat(round.decimalPlaces()).isInstanceOf(Literal.class);
    }

    @Test
    void createsRoundWithNumberOnly() {
        Round round = Round.of(42.5);

        assertThat(round.numericExpression()).isInstanceOf(Literal.class);
        assertThat(round.decimalPlaces()).isInstanceOf(NullScalarExpression.class);
    }

    @Test
    void createsRoundWithScalarExpressionAndDecimalPlaces() {
        ColumnReference column = ColumnReference.of("table", "column");
        Round round = Round.of(column, 3);

        assertThat(round.numericExpression()).isEqualTo(column);
        assertThat(round.decimalPlaces()).isInstanceOf(Literal.class);
    }

    @Test
    void createsRoundWithScalarExpressionOnly() {
        ColumnReference column = ColumnReference.of("products", "price");
        Round round = Round.of(column);

        assertThat(round.numericExpression()).isEqualTo(column);
        assertThat(round.decimalPlaces()).isInstanceOf(NullScalarExpression.class);
    }

    @Test
    void createsRoundWithTwoScalarExpressions() {
        ColumnReference numCol = ColumnReference.of("table1", "value");
        ColumnReference decimalCol = ColumnReference.of("table2", "decimals");
        Round round = new Round(numCol, decimalCol);

        assertThat(round.numericExpression()).isEqualTo(numCol);
        assertThat(round.decimalPlaces()).isEqualTo(decimalCol);
    }

    @Test
    void roundWithZeroDecimalPlaces() {
        Round round = Round.of(123.456, 0);

        assertThat(round.numericExpression()).isNotNull();
        assertThat(round.decimalPlaces()).isNotNull();
    }

    @Test
    void roundWithNegativeDecimalPlaces() {
        Round round = Round.of(12345.6, -2);

        assertThat(round.numericExpression()).isNotNull();
        assertThat(round.decimalPlaces()).isNotNull();
    }

    @Test
    void roundWithLargeNumberOfDecimalPlaces() {
        Round round = Round.of(3.141592653589793, 15);

        assertThat(round.numericExpression()).isNotNull();
        assertThat(round.decimalPlaces()).isNotNull();
    }

    @Test
    void roundWithNegativeNumber() {
        Round round = Round.of(-42.567, 1);

        assertThat(round.numericExpression()).isNotNull();
        assertThat(round.decimalPlaces()).isNotNull();
    }

    @Test
    void roundWithZero() {
        Round round = Round.of(0.0);

        assertThat(round.numericExpression()).isNotNull();
        assertThat(round.decimalPlaces()).isInstanceOf(NullScalarExpression.class);
    }

    @Test
    void roundWithVerySmallNumber() {
        Round round = Round.of(0.000001, 6);

        assertThat(round.numericExpression()).isNotNull();
        assertThat(round.decimalPlaces()).isNotNull();
    }

    @Test
    void roundWithVeryLargeNumber() {
        Round round = Round.of(999999999999.999, 2);

        assertThat(round.numericExpression()).isNotNull();
        assertThat(round.decimalPlaces()).isNotNull();
    }
}
