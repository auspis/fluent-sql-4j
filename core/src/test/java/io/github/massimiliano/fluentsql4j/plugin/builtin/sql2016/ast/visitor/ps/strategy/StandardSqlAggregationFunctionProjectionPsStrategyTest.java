package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.Alias;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.AggregateCallProjection;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAggregationFunctionProjectionPsStrategyTest {

    private StandardSqlAggregationFunctionProjectionPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor specFactory;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlAggregationFunctionProjectionPsStrategy();
        specFactory = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void countWithoutAlias() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall);

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countWithAlias() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("total_users"));

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\") AS \"total_users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void sumWithAlias() {
        AggregateCall aggregateCall = AggregateCall.sum(ColumnReference.of("Order", "amount"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("total_amount"));

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"amount\") AS \"total_amount\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void avgWithoutAlias() {
        AggregateCall aggregateCall = AggregateCall.avg(ColumnReference.of("Product", "price"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall);

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("AVG(\"price\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void maxWithAlias() {
        AggregateCall aggregateCall = AggregateCall.max(ColumnReference.of("Product", "price"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("max_price"));

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("MAX(\"price\") AS \"max_price\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void minWithAlias() {
        AggregateCall aggregateCall = AggregateCall.min(ColumnReference.of("Product", "price"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("min_price"));

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("MIN(\"price\") AS \"min_price\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countStarWithAlias() {
        AggregateCall aggregateCall = AggregateCall.countStar();
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("row_count"));

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(*) AS \"row_count\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countDistinctWithAlias() {
        AggregateCall aggregateCall = AggregateCall.countDistinct(ColumnReference.of("User", "email"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("unique_emails"));

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(DISTINCT \"email\") AS \"unique_emails\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aliasWithEmptyString() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias(""));

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aliasWithBlankString() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("   "));

        PreparedStatementSpec result = strategy.handle(projection, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }
}
