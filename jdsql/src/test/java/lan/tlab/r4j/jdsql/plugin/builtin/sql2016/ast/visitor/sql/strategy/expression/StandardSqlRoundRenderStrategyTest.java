package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Round;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlRoundRenderStrategyTest {

    private StandardSqlRoundRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlRoundRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void number() {
        Round round = Round.of(10.5273);
        String sql = strategy.render(round, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ROUND(10.5273)");
    }

    @Test
    void columnReference() {
        Round round = Round.of(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(round, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ROUND(\"Customer\".\"score\")");
    }

    @Test
    void columnReferenceAndNumber() {
        Round round = Round.of(ColumnReference.of("Customer", "score"), 2);
        String sql = strategy.render(round, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ROUND(\"Customer\".\"score\", 2)");
    }

    @Test
    void numberAndNumber() {
        Round round = Round.of(10.5273, 2);
        String sql = strategy.render(round, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ROUND(10.5273, 2)");
    }

    @Test
    void arithmeticExpressions() {
        Round round = Round.of(
                ArithmeticExpression.division(
                        ColumnReference.of("Customer", "score"), ColumnReference.of("Customer", "age")),
                0);
        String sql = strategy.render(round, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ROUND((\"Customer\".\"score\" / \"Customer\".\"age\"), 0)");
    }
}
