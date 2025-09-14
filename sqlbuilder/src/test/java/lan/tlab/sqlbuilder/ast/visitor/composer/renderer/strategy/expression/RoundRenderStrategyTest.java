package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Round;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoundRenderStrategyTest {

    private RoundRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new RoundRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
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
