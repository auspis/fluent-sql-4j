package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Length;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.dql.clause.Having;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlHavingRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlHavingRenderStrategyTest {

    private StandardSqlHavingRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlHavingRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void noGroupingFunctions() {
        Having having = Having.nullObject();
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
