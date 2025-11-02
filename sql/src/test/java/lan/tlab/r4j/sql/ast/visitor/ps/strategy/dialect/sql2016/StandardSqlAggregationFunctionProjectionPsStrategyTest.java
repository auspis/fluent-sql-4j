package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAggregationFunctionProjectionPsStrategyTest {

    private StandardSqlAggregationFunctionProjectionPsStrategy strategy;
    private PreparedStatementRenderer renderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlAggregationFunctionProjectionPsStrategy();
        renderer = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void countWithoutAlias() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall);

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countWithAlias() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("total_users"));

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\") AS \"total_users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void sumWithAlias() {
        AggregateCall aggregateCall = AggregateCall.sum(ColumnReference.of("Order", "amount"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("total_amount"));

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"amount\") AS \"total_amount\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void avgWithoutAlias() {
        AggregateCall aggregateCall = AggregateCall.avg(ColumnReference.of("Product", "price"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall);

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("AVG(\"price\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void maxWithAlias() {
        AggregateCall aggregateCall = AggregateCall.max(ColumnReference.of("Product", "price"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("max_price"));

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("MAX(\"price\") AS \"max_price\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void minWithAlias() {
        AggregateCall aggregateCall = AggregateCall.min(ColumnReference.of("Product", "price"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("min_price"));

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("MIN(\"price\") AS \"min_price\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countStarWithAlias() {
        AggregateCall aggregateCall = AggregateCall.countStar();
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("row_count"));

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(*) AS \"row_count\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countDistinctWithAlias() {
        AggregateCall aggregateCall = AggregateCall.countDistinct(ColumnReference.of("User", "email"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("unique_emails"));

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(DISTINCT \"email\") AS \"unique_emails\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aliasWithEmptyString() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias(""));

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aliasWithBlankString() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregateCallProjection projection = new AggregateCallProjection(aggregateCall, new Alias("   "));

        PsDto result = strategy.handle(projection, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }
}
