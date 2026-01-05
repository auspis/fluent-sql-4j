package io.github.auspis.fluentsql4j.ast.core.expression.function.number;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.UnaryNumeric;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.UnaryNumeric.FunctionName;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;

class UnaryNumericTest {

    @Test
    void createsAbsWithNumber() {
        UnaryNumeric unary = UnaryNumeric.abs(-42);

        assertThat(unary.functionName()).isEqualTo(FunctionName.ABS);
        assertThat(unary.numericExpression()).isInstanceOf(Literal.class);
    }

    @Test
    void createsAbsWithScalarExpression() {
        ColumnReference column = ColumnReference.of("table", "value");
        UnaryNumeric unary = UnaryNumeric.abs(column);

        assertThat(unary.functionName()).isEqualTo(FunctionName.ABS);
        assertThat(unary.numericExpression()).isEqualTo(column);
    }

    @Test
    void createsCeilWithNumber() {
        UnaryNumeric unary = UnaryNumeric.ceil(3.2);

        assertThat(unary.functionName()).isEqualTo(FunctionName.CEIL);
        assertThat(unary.numericExpression()).isInstanceOf(Literal.class);
    }

    @Test
    void createsCeilWithScalarExpression() {
        ColumnReference column = ColumnReference.of("table", "price");
        UnaryNumeric unary = UnaryNumeric.ceil(column);

        assertThat(unary.functionName()).isEqualTo(FunctionName.CEIL);
        assertThat(unary.numericExpression()).isEqualTo(column);
    }

    @Test
    void createsFloorWithNumber() {
        UnaryNumeric unary = UnaryNumeric.floor(5.9);

        assertThat(unary.functionName()).isEqualTo(FunctionName.FLOOR);
        assertThat(unary.numericExpression()).isInstanceOf(Literal.class);
    }

    @Test
    void createsFloorWithScalarExpression() {
        ColumnReference column = ColumnReference.of("orders", "amount");
        UnaryNumeric unary = UnaryNumeric.floor(column);

        assertThat(unary.functionName()).isEqualTo(FunctionName.FLOOR);
        assertThat(unary.numericExpression()).isEqualTo(column);
    }

    @Test
    void createsSqrtWithNumber() {
        UnaryNumeric unary = UnaryNumeric.sqrt(16);

        assertThat(unary.functionName()).isEqualTo(FunctionName.SQRT);
        assertThat(unary.numericExpression()).isInstanceOf(Literal.class);
    }

    @Test
    void createsSqrtWithScalarExpression() {
        ColumnReference column = ColumnReference.of("table", "value");
        UnaryNumeric unary = UnaryNumeric.sqrt(column);

        assertThat(unary.functionName()).isEqualTo(FunctionName.SQRT);
        assertThat(unary.numericExpression()).isEqualTo(column);
    }

    @Test
    void absWithNegativeValue() {
        UnaryNumeric unary = UnaryNumeric.abs(-123.456);

        assertThat(unary.functionName()).isEqualTo(FunctionName.ABS);
    }

    @Test
    void absWithPositiveValue() {
        UnaryNumeric unary = UnaryNumeric.abs(123.456);

        assertThat(unary.functionName()).isEqualTo(FunctionName.ABS);
    }

    @Test
    void absWithZero() {
        UnaryNumeric unary = UnaryNumeric.abs(0);

        assertThat(unary.functionName()).isEqualTo(FunctionName.ABS);
    }

    @Test
    void ceilWithNegativeValue() {
        UnaryNumeric unary = UnaryNumeric.ceil(-3.2);

        assertThat(unary.functionName()).isEqualTo(FunctionName.CEIL);
    }

    @Test
    void ceilWithIntegerValue() {
        UnaryNumeric unary = UnaryNumeric.ceil(5.0);

        assertThat(unary.functionName()).isEqualTo(FunctionName.CEIL);
    }

    @Test
    void floorWithNegativeValue() {
        UnaryNumeric unary = UnaryNumeric.floor(-5.9);

        assertThat(unary.functionName()).isEqualTo(FunctionName.FLOOR);
    }

    @Test
    void sqrtWithLargeNumber() {
        UnaryNumeric unary = UnaryNumeric.sqrt(1000000);

        assertThat(unary.functionName()).isEqualTo(FunctionName.SQRT);
    }

    @Test
    void sqrtWithSmallNumber() {
        UnaryNumeric unary = UnaryNumeric.sqrt(0.0001);

        assertThat(unary.functionName()).isEqualTo(FunctionName.SQRT);
    }
}
