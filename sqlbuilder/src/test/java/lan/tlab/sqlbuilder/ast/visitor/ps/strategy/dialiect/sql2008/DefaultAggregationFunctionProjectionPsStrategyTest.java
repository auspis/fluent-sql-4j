package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultAggregationFunctionProjectionPsStrategyTest {

    private DefaultAggregationFunctionProjectionPsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultAggregationFunctionProjectionPsStrategy();
        visitor = new PreparedStatementVisitor();
        ctx = new AstContext();
    }

    @Test
    void countWithoutAlias() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregationFunctionProjection projection = new AggregationFunctionProjection(aggregateCall);

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countWithAlias() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregationFunctionProjection projection =
                new AggregationFunctionProjection(aggregateCall, new As("total_users"));

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\") AS \"total_users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void sumWithAlias() {
        AggregateCall aggregateCall = AggregateCall.sum(ColumnReference.of("Order", "amount"));
        AggregationFunctionProjection projection =
                new AggregationFunctionProjection(aggregateCall, new As("total_amount"));

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"amount\") AS \"total_amount\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void avgWithoutAlias() {
        AggregateCall aggregateCall = AggregateCall.avg(ColumnReference.of("Product", "price"));
        AggregationFunctionProjection projection = new AggregationFunctionProjection(aggregateCall);

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("AVG(\"price\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void maxWithAlias() {
        AggregateCall aggregateCall = AggregateCall.max(ColumnReference.of("Product", "price"));
        AggregationFunctionProjection projection =
                new AggregationFunctionProjection(aggregateCall, new As("max_price"));

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MAX(\"price\") AS \"max_price\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void minWithAlias() {
        AggregateCall aggregateCall = AggregateCall.min(ColumnReference.of("Product", "price"));
        AggregationFunctionProjection projection =
                new AggregationFunctionProjection(aggregateCall, new As("min_price"));

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MIN(\"price\") AS \"min_price\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countStarWithAlias() {
        AggregateCall aggregateCall = AggregateCall.countStar();
        AggregationFunctionProjection projection =
                new AggregationFunctionProjection(aggregateCall, new As("row_count"));

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(*) AS \"row_count\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countDistinctWithAlias() {
        AggregateCall aggregateCall = AggregateCall.countDistinct(ColumnReference.of("User", "email"));
        AggregationFunctionProjection projection =
                new AggregationFunctionProjection(aggregateCall, new As("unique_emails"));

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(DISTINCT \"email\") AS \"unique_emails\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aliasWithEmptyString() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregationFunctionProjection projection = new AggregationFunctionProjection(aggregateCall, new As(""));

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void aliasWithBlankString() {
        AggregateCall aggregateCall = AggregateCall.count(ColumnReference.of("User", "id"));
        AggregationFunctionProjection projection = new AggregationFunctionProjection(aggregateCall, new As("   "));

        PsDto result = strategy.handle(projection, visitor, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"id\")");
        assertThat(result.parameters()).isEmpty();
    }
}
