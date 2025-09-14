package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AggregateCallRenderStrategyTest {

    private AggregateCallRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new AggregateCallRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
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
