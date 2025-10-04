package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultHavingClausePsStrategyTest {

    private DefaultHavingClausePsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultHavingClausePsStrategy();
        visitor = new PreparedStatementVisitor();
        ctx = new AstContext();
    }

    @Test
    void nullCondition() {
        Having having = Having.builder().build();

        PsDto result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void nullBooleanExpression() {
        Having having = Having.of(new NullBooleanExpression());

        PsDto result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void simpleComparison() {
        Having having = Having.of(Comparison.gt(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(1)));

        PsDto result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\") > ?");
        assertThat(result.parameters()).containsExactly(1);
    }

    @Test
    void aggregateComparison() {
        Having having =
                Having.of(Comparison.gte(AggregateCall.sum(ColumnReference.of("Order", "amount")), Literal.of(1000)));

        PsDto result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"amount\") >= ?");
        assertThat(result.parameters()).containsExactly(1000);
    }

    @Test
    void averageComparison() {
        Having having =
                Having.of(Comparison.lt(AggregateCall.avg(ColumnReference.of("Product", "price")), Literal.of(50.0)));

        PsDto result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEqualTo("AVG(\"price\") < ?");
        assertThat(result.parameters()).containsExactly(50.0);
    }
}
