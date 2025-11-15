package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlBinaryArithmeticExpressionPsStrategyTest {

    private StandardSqlBinaryArithmeticExpressionPsStrategy strategy;
    private PreparedStatementRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlBinaryArithmeticExpressionPsStrategy();
        renderer = new PreparedStatementRenderer();
    }

    @Test
    void additionLiterals() {
        BinaryArithmeticExpression expression = ArithmeticExpression.addition(Literal.of(5), Literal.of(23));
        PsDto result = strategy.handle(expression, renderer, new AstContext());
        assertThat(result.sql()).isEqualTo("(? + ?)");
        assertThat(result.parameters()).containsExactly(5, 23);
    }

    @Test
    void subtractionLiterals() {
        BinaryArithmeticExpression expression = ArithmeticExpression.subtraction(Literal.of(10), Literal.of(3));
        PsDto result = strategy.handle(expression, renderer, new AstContext());
        assertThat(result.sql()).isEqualTo("(? - ?)");
        assertThat(result.parameters()).containsExactly(10, 3);
    }

    @Test
    void multiplicationLiterals() {
        BinaryArithmeticExpression expression = ArithmeticExpression.multiplication(Literal.of(4), Literal.of(5));
        PsDto result = strategy.handle(expression, renderer, new AstContext());
        assertThat(result.sql()).isEqualTo("(? * ?)");
        assertThat(result.parameters()).containsExactly(4, 5);
    }

    @Test
    void divisionLiterals() {
        BinaryArithmeticExpression expression = ArithmeticExpression.division(Literal.of(10), Literal.of(2));
        PsDto result = strategy.handle(expression, renderer, new AstContext());
        assertThat(result.sql()).isEqualTo("(? / ?)");
        assertThat(result.parameters()).containsExactly(10, 2);
    }

    @Test
    void moduloLiterals() {
        BinaryArithmeticExpression expression = ArithmeticExpression.modulo(Literal.of(10), Literal.of(3));
        PsDto result = strategy.handle(expression, renderer, new AstContext());
        assertThat(result.sql()).isEqualTo("(? % ?)");
        assertThat(result.parameters()).containsExactly(10, 3);
    }

    @Test
    void columnReferenceAndLiteral() {
        BinaryArithmeticExpression expression =
                ArithmeticExpression.addition(ColumnReference.of("Customer", "score"), Literal.of(10));
        PsDto result = strategy.handle(expression, renderer, new AstContext());
        assertThat(result.sql()).isEqualTo("(\"score\" + ?)");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void twoColumnReferences() {
        BinaryArithmeticExpression expression = ArithmeticExpression.multiplication(
                ColumnReference.of("Order", "quantity"), ColumnReference.of("Product", "price"));
        PsDto result = strategy.handle(expression, renderer, new AstContext());
        assertThat(result.sql()).isEqualTo("(\"quantity\" * \"price\")");
        assertThat(result.parameters()).isEmpty();
    }
}
