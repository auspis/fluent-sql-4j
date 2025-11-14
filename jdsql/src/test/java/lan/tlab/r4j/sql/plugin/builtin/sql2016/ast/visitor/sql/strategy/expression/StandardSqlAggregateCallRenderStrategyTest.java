package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAggregateCallRenderStrategyTest {

    private StandardSqlAggregateCallRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlAggregateCallRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void max() {
        AggregateCall exp = AggregateCall.max(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MAX(\"Customer\".\"score\")");
    }

    @Test
    void min() {
        AggregateCall exp = AggregateCall.min(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MIN(\"Customer\".\"score\")");
    }

    @Test
    void avg() {
        AggregateCall exp = AggregateCall.avg(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("AVG(\"Customer\".\"score\")");
    }

    @Test
    void sum() {
        AggregateCall exp = AggregateCall.sum(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SUM(\"Customer\".\"score\")");
    }

    @Test
    void count() {
        AggregateCall exp = AggregateCall.count(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("COUNT(\"Customer\".\"score\")");
    }

    @Test
    void countStar() {
        AggregateCall exp = AggregateCall.countStar();
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("COUNT(*)");
    }

    @Test
    void countDistinct() {
        AggregateCall exp = AggregateCall.countDistinct(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("COUNT(DISTINCT \"Customer\".\"score\")");
    }
}
