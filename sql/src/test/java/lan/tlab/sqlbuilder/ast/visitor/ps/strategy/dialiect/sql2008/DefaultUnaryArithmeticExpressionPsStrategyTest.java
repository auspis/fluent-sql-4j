package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultUnaryArithmeticExpressionPsStrategyTest {

    private DefaultUnaryArithmeticExpressionPsStrategy strategy;
    private PreparedStatementVisitor visitor;

    @BeforeEach
    void setUp() {
        strategy = new DefaultUnaryArithmeticExpressionPsStrategy();
        visitor = new PreparedStatementVisitor();
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
