package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AggregationFunctionProjectionRenderStrategyTest {

    private AggregationFunctionProjectionRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new AggregationFunctionProjectionRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        AggregationFunctionProjection function =
                new AggregationFunctionProjection(AggregateCall.max(ColumnReference.of("Customer", "score")));
        String sql = strategy.render(function, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MAX(\"Customer\".\"score\")");
    }

    @Test
    void as() {
        AggregationFunctionProjection function = new AggregationFunctionProjection(
                AggregateCall.max(ColumnReference.of("Customer", "score")), "topScore");
        String sql = strategy.render(function, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MAX(\"Customer\".\"score\") AS topScore");
    }
}
