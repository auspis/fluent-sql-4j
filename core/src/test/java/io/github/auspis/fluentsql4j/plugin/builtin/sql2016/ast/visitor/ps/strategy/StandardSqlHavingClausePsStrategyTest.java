package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.dql.clause.Having;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlHavingClausePsStrategyTest {

    private StandardSqlHavingClausePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlHavingClausePsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void nullCondition() {
        Having having = Having.nullObject();

        PreparedStatementSpec result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void nullPredicate() {
        Having having = Having.of(new NullPredicate());

        PreparedStatementSpec result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void simpleComparison() {
        Having having = Having.of(Comparison.gt(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(1)));

        PreparedStatementSpec result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\") > ?");
        assertThat(result.parameters()).containsExactly(1);
    }

    @Test
    void aggregateComparison() {
        Having having =
                Having.of(Comparison.gte(AggregateCall.sum(ColumnReference.of("Order", "amount")), Literal.of(1000)));

        PreparedStatementSpec result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"amount\") >= ?");
        assertThat(result.parameters()).containsExactly(1000);
    }

    @Test
    void averageComparison() {
        Having having =
                Having.of(Comparison.lt(AggregateCall.avg(ColumnReference.of("Product", "price")), Literal.of(50.0)));

        PreparedStatementSpec result = strategy.handle(having, visitor, ctx);

        assertThat(result.sql()).isEqualTo("AVG(\"price\") < ?");
        assertThat(result.parameters()).containsExactly(50.0);
    }
}
