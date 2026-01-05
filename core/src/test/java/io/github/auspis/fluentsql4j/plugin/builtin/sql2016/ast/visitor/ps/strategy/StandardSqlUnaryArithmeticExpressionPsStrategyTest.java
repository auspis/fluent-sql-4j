package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlUnaryArithmeticExpressionPsStrategyTest {

    private StandardSqlUnaryArithmeticExpressionPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlUnaryArithmeticExpressionPsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
    }

    @Test
    void negationLiteral() {
        UnaryArithmeticExpression expression = ArithmeticExpression.negation(Literal.of(10));
        PreparedStatementSpec result = strategy.handle(expression, visitor, new AstContext());
        assertThat(result.sql()).isEqualTo("(-?)");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void negationColumnReference() {
        UnaryArithmeticExpression expression = ArithmeticExpression.negation(ColumnReference.of("Customer", "score"));
        PreparedStatementSpec result = strategy.handle(expression, visitor, new AstContext());
        assertThat(result.sql()).isEqualTo("(-\"score\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void nestedNegation() {
        UnaryArithmeticExpression inner = ArithmeticExpression.negation(Literal.of(5));
        UnaryArithmeticExpression outer = ArithmeticExpression.negation(inner);
        PreparedStatementSpec result = strategy.handle(outer, visitor, new AstContext());
        assertThat(result.sql()).isEqualTo("(-(-?))");
        assertThat(result.parameters()).containsExactly(5);
    }
}
