package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AggregateCallProjectionRenderStrategyTest {

    private AggregateCallProjectionRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new AggregateCallProjectionRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        AggregateCallProjection function =
                new AggregateCallProjection(AggregateCall.max(ColumnReference.of("Customer", "score")));
        String sql = strategy.render(function, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MAX(\"Customer\".\"score\")");
    }

    @Test
    void as() {
        AggregateCallProjection function =
                new AggregateCallProjection(AggregateCall.max(ColumnReference.of("Customer", "score")), "topScore");
        String sql = strategy.render(function, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MAX(\"Customer\".\"score\") AS topScore");
    }
}
