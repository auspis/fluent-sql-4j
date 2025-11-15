package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlUnaryArithmeticExpressionPsStrategyTest {

    private StandardSqlUnaryArithmeticExpressionPsStrategy strategy;
    private PreparedStatementRenderer visitor;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlUnaryArithmeticExpressionPsStrategy();
        visitor = new PreparedStatementRenderer();
    }

    @Test
    void negationLiteral() {
        UnaryArithmeticExpression expression = ArithmeticExpression.negation(Literal.of(10));
        PsDto result = strategy.handle(expression, visitor, new AstContext());
        assertThat(result.sql()).isEqualTo("(-?)");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void negationColumnReference() {
        UnaryArithmeticExpression expression = ArithmeticExpression.negation(ColumnReference.of("Customer", "score"));
        PsDto result = strategy.handle(expression, visitor, new AstContext());
        assertThat(result.sql()).isEqualTo("(-\"score\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void nestedNegation() {
        UnaryArithmeticExpression inner = ArithmeticExpression.negation(Literal.of(5));
        UnaryArithmeticExpression outer = ArithmeticExpression.negation(inner);
        PsDto result = strategy.handle(outer, visitor, new AstContext());
        assertThat(result.sql()).isEqualTo("(-(-?))");
        assertThat(result.parameters()).containsExactly(5);
    }
}
