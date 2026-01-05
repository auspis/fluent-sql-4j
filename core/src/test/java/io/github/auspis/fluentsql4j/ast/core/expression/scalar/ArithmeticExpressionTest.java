package io.github.auspis.fluentsql4j.ast.core.expression.scalar;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ArithmeticExpressionTest {

    @Test
    void createsBinaryAdditionWithLiterals() {
        BinaryArithmeticExpression expr = ArithmeticExpression.addition(Literal.of(5), Literal.of(3));

        assertThat(expr.lhs()).isInstanceOf(Literal.class);
        assertThat(expr.operator()).isEqualTo("+");
        assertThat(expr.rhs()).isInstanceOf(Literal.class);
    }

    @Test
    void createsBinaryAdditionWithColumns() {
        ColumnReference col1 = ColumnReference.of("table", "price");
        ColumnReference col2 = ColumnReference.of("table", "tax");
        BinaryArithmeticExpression expr = ArithmeticExpression.addition(col1, col2);

        assertThat(expr.lhs()).isEqualTo(col1);
        assertThat(expr.operator()).isEqualTo("+");
        assertThat(expr.rhs()).isEqualTo(col2);
    }

    @Test
    void createsBinaryAdditionMixed() {
        ColumnReference col = ColumnReference.of("table", "value");
        Literal<Number> lit = Literal.of(100);
        BinaryArithmeticExpression expr = ArithmeticExpression.addition(col, lit);

        assertThat(expr.lhs()).isEqualTo(col);
        assertThat(expr.operator()).isEqualTo("+");
        assertThat(expr.rhs()).isEqualTo(lit);
    }

    @Test
    void createsBinarySubtractionWithLiterals() {
        BinaryArithmeticExpression expr = ArithmeticExpression.subtraction(Literal.of(10), Literal.of(4));

        assertThat(expr.lhs()).isInstanceOf(Literal.class);
        assertThat(expr.operator()).isEqualTo("-");
        assertThat(expr.rhs()).isInstanceOf(Literal.class);
    }

    @Test
    void createsBinarySubtractionWithColumns() {
        ColumnReference col1 = ColumnReference.of("t1", "a");
        ColumnReference col2 = ColumnReference.of("t2", "b");
        BinaryArithmeticExpression expr = ArithmeticExpression.subtraction(col1, col2);

        assertThat(expr.operator()).isEqualTo("-");
        assertThat(expr.lhs()).isEqualTo(col1);
        assertThat(expr.rhs()).isEqualTo(col2);
    }

    @Test
    void createsBinaryMultiplicationWithLiterals() {
        BinaryArithmeticExpression expr = ArithmeticExpression.multiplication(Literal.of(6), Literal.of(7));

        assertThat(expr.lhs()).isInstanceOf(Literal.class);
        assertThat(expr.operator()).isEqualTo("*");
        assertThat(expr.rhs()).isInstanceOf(Literal.class);
    }

    @Test
    void createsBinaryMultiplicationWithColumns() {
        ColumnReference col1 = ColumnReference.of("orders", "quantity");
        ColumnReference col2 = ColumnReference.of("orders", "unitPrice");
        BinaryArithmeticExpression expr = ArithmeticExpression.multiplication(col1, col2);

        assertThat(expr.operator()).isEqualTo("*");
        assertThat(expr.lhs()).isEqualTo(col1);
        assertThat(expr.rhs()).isEqualTo(col2);
    }

    @Test
    void createsBinaryDivisionWithLiterals() {
        BinaryArithmeticExpression expr = ArithmeticExpression.division(Literal.of(20), Literal.of(4));

        assertThat(expr.lhs()).isInstanceOf(Literal.class);
        assertThat(expr.operator()).isEqualTo("/");
        assertThat(expr.rhs()).isInstanceOf(Literal.class);
    }

    @Test
    void createsBinaryDivisionWithColumns() {
        ColumnReference col1 = ColumnReference.of("stats", "total");
        ColumnReference col2 = ColumnReference.of("stats", "count");
        BinaryArithmeticExpression expr = ArithmeticExpression.division(col1, col2);

        assertThat(expr.operator()).isEqualTo("/");
        assertThat(expr.lhs()).isEqualTo(col1);
        assertThat(expr.rhs()).isEqualTo(col2);
    }

    @Test
    void createsBinaryModuloWithLiterals() {
        BinaryArithmeticExpression expr = ArithmeticExpression.modulo(Literal.of(17), Literal.of(5));

        assertThat(expr.lhs()).isInstanceOf(Literal.class);
        assertThat(expr.operator()).isEqualTo("%");
        assertThat(expr.rhs()).isInstanceOf(Literal.class);
    }

    @Test
    void createsBinaryModuloWithColumns() {
        ColumnReference col1 = ColumnReference.of("numbers", "dividend");
        ColumnReference col2 = ColumnReference.of("numbers", "divisor");
        BinaryArithmeticExpression expr = ArithmeticExpression.modulo(col1, col2);

        assertThat(expr.operator()).isEqualTo("%");
        assertThat(expr.lhs()).isEqualTo(col1);
        assertThat(expr.rhs()).isEqualTo(col2);
    }

    @Test
    void createsUnaryNegationWithLiteral() {
        UnaryArithmeticExpression expr = ArithmeticExpression.negation(Literal.of(42));

        assertThat(expr.operator()).isEqualTo("-");
        assertThat(expr.expression()).isInstanceOf(Literal.class);
    }

    @Test
    void createsUnaryNegationWithColumn() {
        ColumnReference col = ColumnReference.of("table", "value");
        UnaryArithmeticExpression expr = ArithmeticExpression.negation(col);

        assertThat(expr.operator()).isEqualTo("-");
        assertThat(expr.expression()).isEqualTo(col);
    }

    @Test
    void createsUnaryNegationWithNull() {
        NullScalarExpression nullExpr = new NullScalarExpression();
        UnaryArithmeticExpression expr = ArithmeticExpression.negation(nullExpr);

        assertThat(expr.operator()).isEqualTo("-");
        assertThat(expr.expression()).isEqualTo(nullExpr);
    }

    @Test
    void chainedArithmeticExpressions() {
        ColumnReference col = ColumnReference.of("t", "x");
        Literal<Number> lit1 = Literal.of(10);
        Literal<Number> lit2 = Literal.of(5);
        BinaryArithmeticExpression expr =
                ArithmeticExpression.addition(ArithmeticExpression.multiplication(col, lit1), lit2);

        assertThat(expr.operator()).isEqualTo("+");
        assertThat(expr.lhs()).isInstanceOf(BinaryArithmeticExpression.class);
        assertThat(expr.rhs()).isEqualTo(lit2);
    }

    @Test
    void binaryArithmeticAcceptsVisitor() {
        BinaryArithmeticExpression expr = ArithmeticExpression.addition(Literal.of(1), Literal.of(2));
        @SuppressWarnings("unchecked")
        Visitor<String> visitor = Mockito.mock(Visitor.class);
        AstContext ctx = new AstContext();
        Mockito.when(visitor.visit(expr, ctx)).thenReturn("visited");

        String result = expr.accept(visitor, ctx);

        assertThat(result).isEqualTo("visited");
        Mockito.verify(visitor).visit(expr, ctx);
    }

    @Test
    void unaryArithmeticAcceptsVisitor() {
        UnaryArithmeticExpression expr = ArithmeticExpression.negation(Literal.of(42));
        @SuppressWarnings("unchecked")
        Visitor<String> visitor = Mockito.mock(Visitor.class);
        AstContext ctx = new AstContext();
        Mockito.when(visitor.visit(expr, ctx)).thenReturn("negated");

        String result = expr.accept(visitor, ctx);

        assertThat(result).isEqualTo("negated");
        Mockito.verify(visitor).visit(expr, ctx);
    }

    @Test
    void complexArithmeticWithMultipleOperators() {
        ColumnReference qty = ColumnReference.of("orders", "qty");
        ColumnReference price = ColumnReference.of("orders", "price");
        Literal<Number> discount = Literal.of(0.1);

        // (qty * price) * (1 - discount)
        BinaryArithmeticExpression qtyTimesPrice = ArithmeticExpression.multiplication(qty, price);
        BinaryArithmeticExpression oneMinusDiscount = ArithmeticExpression.subtraction(Literal.of(1), discount);
        BinaryArithmeticExpression totalPrice = ArithmeticExpression.multiplication(qtyTimesPrice, oneMinusDiscount);

        assertThat(totalPrice.lhs()).isEqualTo(qtyTimesPrice);
        assertThat(totalPrice.operator()).isEqualTo("*");
        assertThat(totalPrice.rhs()).isEqualTo(oneMinusDiscount);
    }

    @Test
    void negationOfComplexExpression() {
        ColumnReference col1 = ColumnReference.of("t", "a");
        ColumnReference col2 = ColumnReference.of("t", "b");
        BinaryArithmeticExpression addition = ArithmeticExpression.addition(col1, col2);
        UnaryArithmeticExpression negated = ArithmeticExpression.negation(addition);

        assertThat(negated.operator()).isEqualTo("-");
        assertThat(negated.expression()).isEqualTo(addition);
    }

    @Test
    void binaryExpressionEquality() {
        BinaryArithmeticExpression expr1 = ArithmeticExpression.addition(Literal.of(5), Literal.of(3));
        BinaryArithmeticExpression expr2 = ArithmeticExpression.addition(Literal.of(5), Literal.of(3));

        assertThat(expr1).isEqualTo(expr2);
    }

    @Test
    void unaryExpressionEquality() {
        UnaryArithmeticExpression expr1 = ArithmeticExpression.negation(Literal.of(42));
        UnaryArithmeticExpression expr2 = ArithmeticExpression.negation(Literal.of(42));

        assertThat(expr1).isEqualTo(expr2);
    }

    @Test
    void binaryExpressionWithNullValues() {
        NullScalarExpression nullExpr = new NullScalarExpression();
        BinaryArithmeticExpression expr = ArithmeticExpression.addition(Literal.of(5), nullExpr);

        assertThat(expr.lhs()).isInstanceOf(Literal.class);
        assertThat(expr.rhs()).isEqualTo(nullExpr);
        assertThat(expr.operator()).isEqualTo("+");
    }
}
