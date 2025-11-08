package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAggregateCallPsStrategyTest {

    private StandardSqlAggregateCallPsStrategy strategy;
    private PreparedStatementRenderer renderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlAggregateCallPsStrategy();
        renderer = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void sum() {
        ColumnReference column = ColumnReference.of("table", "column");
        AggregateCall aggregateCall = AggregateCall.sum(column);

        PsDto result = strategy.handle(aggregateCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("SUM(\"column\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void count() {
        ColumnReference column = ColumnReference.of("table", "column");
        AggregateCall aggregateCall = AggregateCall.count(column);

        PsDto result = strategy.handle(aggregateCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(\"column\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countDistinct() {
        ColumnReference column = ColumnReference.of("table", "column");
        AggregateCall aggregateCall = AggregateCall.countDistinct(column);

        PsDto result = strategy.handle(aggregateCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(DISTINCT \"column\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void countStar() {
        AggregateCall aggregateCall = AggregateCall.countStar();

        PsDto result = strategy.handle(aggregateCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("COUNT(*)");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void allAggregateOperators() {
        ColumnReference column = ColumnReference.of("table", "id");

        assertThat(strategy.handle(AggregateCall.avg(column), renderer, ctx).sql())
                .isEqualTo("AVG(\"id\")");
        assertThat(strategy.handle(AggregateCall.max(column), renderer, ctx).sql())
                .isEqualTo("MAX(\"id\")");
        assertThat(strategy.handle(AggregateCall.min(column), renderer, ctx).sql())
                .isEqualTo("MIN(\"id\")");
    }
}
