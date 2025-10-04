package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Length;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HavingRenderStrategyTest {

    private HavingRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new HavingRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void noGroupingFunctions() {
        Having having = Having.builder().build();
        String sql = strategy.render(having, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }

    @Test
    void ok() {
        Having having = Having.of(Comparison.gt(new Length(ColumnReference.of("Customer", "name")), Literal.of(1)));
        String sql = strategy.render(having, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("HAVING LENGTH(\"Customer\".\"name\") > 1");
    }

    @Test
    void aggregationFunction() {
        Having having =
                Having.of(Comparison.gt(AggregateCall.sum(ColumnReference.of("Customer", "score")), Literal.of(10)));
        String sql = strategy.render(having, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("HAVING SUM(\"Customer\".\"score\") > 10");
    }
}
