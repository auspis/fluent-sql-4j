package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.dql.clause.Having;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlHavingClausePsStrategyTest {

    private StandardSqlHavingClausePsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlHavingClausePsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void nullCondition() {
        Having having = Having.nullObject();

        PsDto result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void nullPredicate() {
        Having having = Having.of(new NullPredicate());

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
