package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAggregateCallPsStrategyTest {

    private StandardSqlAggregateCallPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor specFactory;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlAggregateCallPsStrategy();
        specFactory = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void sum() {
        ColumnReference column = ColumnReference.of("table", "column");
        AggregateCall aggregateCall = AggregateCall.sum(column);

        PreparedStatementSpec result = strategy.handle(aggregateCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"column\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void count() {
        ColumnReference column = ColumnReference.of("table", "column");
        AggregateCall aggregateCall = AggregateCall.count(column);

        PreparedStatementSpec result = strategy.handle(aggregateCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"column\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countDistinct() {
        ColumnReference column = ColumnReference.of("table", "column");
        AggregateCall aggregateCall = AggregateCall.countDistinct(column);

        PreparedStatementSpec result = strategy.handle(aggregateCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(DISTINCT \"column\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countStar() {
        AggregateCall aggregateCall = AggregateCall.countStar();

        PreparedStatementSpec result = strategy.handle(aggregateCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(*)");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void allAggregateOperators() {
        ColumnReference column = ColumnReference.of("table", "id");

        assertThat(strategy.handle(AggregateCall.avg(column), specFactory, ctx).sql())
                .isEqualTo("AVG(\"id\")");
        assertThat(strategy.handle(AggregateCall.max(column), specFactory, ctx).sql())
                .isEqualTo("MAX(\"id\")");
        assertThat(strategy.handle(AggregateCall.min(column), specFactory, ctx).sql())
                .isEqualTo("MIN(\"id\")");
    }
}
